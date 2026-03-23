(function () {
    const { formatJson, requestJson, safeJsonParse, setStatus } = window.appShared;

    const HISTORY_ENDPOINT = '/api/user/orchestration/workflows/currency-conversion/history';

    const form = document.getElementById('workflow-form');
    const promptField = document.getElementById('currency-prompt');
    const statusPill = document.getElementById('user-status');
    const exampleButton = document.getElementById('load-example');
    const refreshHistoryButton = document.getElementById('refresh-history');
    const historyEmpty = document.getElementById('history-empty');
    const historyList = document.getElementById('history-list');
    const emptyState = document.getElementById('result-empty');
    const resultContent = document.getElementById('result-content');
    const summary = document.getElementById('conversion-summary');
    const caption = document.getElementById('conversion-caption');
    const planId = document.getElementById('plan-id');
    const instanceId = document.getElementById('instance-id');
    const executionStatus = document.getElementById('execution-status');
    const exchangeRate = document.getElementById('exchange-rate');
    const rateSource = document.getElementById('rate-source');
    const completedAt = document.getElementById('completed-at');
    const rawPayload = document.getElementById('raw-payload');
    const stepList = document.getElementById('step-list');

    let historyItems = [];
    let selectedInstanceId = null;

    exampleButton.addEventListener('click', function () {
        promptField.value = 'Convert 125 USD to GBP';
        promptField.focus();
    });

    refreshHistoryButton.addEventListener('click', function () {
        loadHistory(selectedInstanceId);
    });

    form.addEventListener('submit', async function (event) {
        event.preventDefault();

        const prompt = promptField.value.trim();
        if (!prompt) {
            return;
        }

        setStatus(statusPill, 'Running…', 'loading');

        try {
            const response = await requestJson('/api/user/orchestration/workflows/currency-conversion/execute', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ prompt })
            });

            if (!response.ok) {
                renderError(
                    response.status === 400
                        ? 'Use a prompt like “Convert 100 USD to EUR”.'
                        : 'The workflow returned an unexpected error.',
                    response.data || response.text
                );
                setStatus(statusPill, 'Request failed', 'error');
                return;
            }

            const details = {
                instance: {
                    instanceId: response.data.instanceId,
                    planId: response.data.planId,
                    status: 'COMPLETED',
                    completedAt: new Date().toISOString()
                },
                payload: safeJsonParse(response.data.outputPayload) || {},
                steps: []
            };

            await loadInstanceDetails(response.data.instanceId, details);
            await loadHistory(response.data.instanceId);
            setStatus(statusPill, 'Completed', 'success');
        } catch (error) {
            renderError('Check that the backend is running and try again.', String(error), 'Unable to reach the service');
            setStatus(statusPill, 'Network error', 'error');
        }
    });

    loadHistory();

    async function loadHistory(instanceToSelect) {
        try {
            const response = await requestJson(HISTORY_ENDPOINT);
            historyItems = response.ok && Array.isArray(response.data) ? response.data : [];
            renderHistory(historyItems);

            const nextInstanceId = instanceToSelect
                || (selectedInstanceId && historyItems.some(function (item) { return item.instanceId === selectedInstanceId; })
                    ? selectedInstanceId
                    : null)
                || (historyItems.length ? historyItems[0].instanceId : null);

            if (nextInstanceId) {
                await loadInstanceDetails(nextInstanceId);
            }
        } catch (error) {
            historyEmpty.textContent = 'Unable to load workflow history right now.';
            historyEmpty.classList.remove('hidden');
            historyList.classList.add('hidden');
        }
    }

    function renderHistory(instances) {
        if (!instances.length) {
            historyEmpty.classList.remove('hidden');
            historyList.classList.add('hidden');
            historyList.innerHTML = '';
            return;
        }

        historyEmpty.classList.add('hidden');
        historyList.classList.remove('hidden');
        historyList.innerHTML = instances.map(function (instance) {
            const createdLabel = formatDate(instance.createdAt || instance.startedAt);
            return `
                <button type="button" class="history-item${instance.instanceId === selectedInstanceId ? ' active' : ''}" data-instance-id="${instance.instanceId}">
                    <div>
                        <strong>${instance.instanceId}</strong>
                        <p class="subtle">${createdLabel}</p>
                    </div>
                    <span class="history-status">${instance.status}</span>
                </button>
            `;
        }).join('');

        historyList.querySelectorAll('[data-instance-id]').forEach(function (button) {
            button.addEventListener('click', function () {
                loadInstanceDetails(button.dataset.instanceId);
            });
        });
    }

    async function loadInstanceDetails(instanceIdValue, preloaded) {
        selectedInstanceId = instanceIdValue;
        renderHistory(historyItems);
        setStatus(statusPill, 'Loading instance…', 'loading');

        try {
            const instanceResponse = preloaded?.instance
                ? { ok: true, data: preloaded.instance }
                : await requestJson(`/api/user/orchestration/executions/${instanceIdValue}`);
            const stepsResponse = preloaded?.steps?.length
                ? { ok: true, data: preloaded.steps }
                : await requestJson(`/api/user/orchestration/executions/${instanceIdValue}/steps`);

            if (!instanceResponse.ok) {
                renderError('The selected instance could not be loaded.', instanceResponse.data || instanceResponse.text);
                setStatus(statusPill, 'Request failed', 'error');
                return;
            }

            const steps = Array.isArray(stepsResponse.data) ? stepsResponse.data : [];
            const payload = preloaded?.payload || resolvePrimaryPayload(instanceResponse.data, steps);
            renderResult(instanceResponse.data, payload, steps);
            setStatus(statusPill, 'History loaded', 'success');
        } catch (error) {
            renderError('The selected instance could not be loaded.', String(error));
            setStatus(statusPill, 'Network error', 'error');
        }
    }

    function resolvePrimaryPayload(instance, steps) {
        const finishedStep = [...steps].reverse().find(function (step) {
            return step.outputPayload;
        });

        if (finishedStep) {
            return safeJsonParse(finishedStep.outputPayload) || finishedStep.outputPayload;
        }

        const errorPayload = safeJsonParse(instance.errorPayload);
        return errorPayload || instance.errorPayload || {};
    }

    function renderError(message, payload, title) {
        emptyState.classList.add('hidden');
        resultContent.classList.remove('hidden');
        summary.textContent = title || 'Unable to execute workflow';
        caption.textContent = message;
        planId.textContent = '—';
        instanceId.textContent = '—';
        executionStatus.textContent = '—';
        exchangeRate.textContent = '—';
        rateSource.textContent = '—';
        completedAt.textContent = '—';
        rawPayload.textContent = formatJson(payload);
        stepList.innerHTML = '<div class="empty-state">No workflow steps were returned.</div>';
    }

    function renderResult(execution, payload, steps) {
        emptyState.classList.add('hidden');
        resultContent.classList.remove('hidden');

        const payloadObject = typeof payload === 'object' && payload !== null ? payload : {};
        summary.textContent = payloadObject.converted_amount !== undefined
            ? `${payloadObject.source_amount} ${payloadObject.source_currency} → ${payloadObject.converted_amount} ${payloadObject.target_currency}`
            : execution.status === 'FAILED'
                ? 'Workflow ended with an error'
                : 'Workflow finished';
        caption.textContent = payloadObject.rate_timestamp
            ? `Quoted at ${payloadObject.rate_timestamp}${payloadObject.rate_stale ? ' (stale rate)' : ''}.`
            : execution.completedAt
                ? `Completed ${formatDate(execution.completedAt)}.`
                : 'Execution details loaded.';
        planId.textContent = execution.planId || '—';
        instanceId.textContent = execution.instanceId || '—';
        executionStatus.textContent = execution.status || '—';
        exchangeRate.textContent = payloadObject.exchange_rate ?? '—';
        rateSource.textContent = payloadObject.rate_source ?? '—';
        completedAt.textContent = formatDate(execution.completedAt);
        rawPayload.textContent = formatJson(payload);

        if (!steps.length) {
            stepList.innerHTML = '<div class="empty-state">No timeline steps were returned for this instance.</div>';
            return;
        }

        stepList.innerHTML = steps.map(function (step) {
            const output = safeJsonParse(step.outputPayload);
            const detail = output && typeof output === 'object'
                ? Object.entries(output)
                    .slice(0, 3)
                    .map(function (entry) { return `${entry[0]}: ${entry[1]}`; })
                    .join(' • ')
                : (step.outputPayload || step.errorMessage || 'No payload');

            return `
                <article class="step-item timeline-item">
                    <div class="timeline-marker" aria-hidden="true"></div>
                    <div class="timeline-content">
                        <div>
                            <h3>${step.stepId}</h3>
                            <p class="subtle">${detail}</p>
                        </div>
                        <div class="step-meta">
                            <strong>${step.status}</strong>
                            <div>Started: ${formatDate(step.startedAt)}</div>
                            <div>Completed: ${formatDate(step.completedAt)}</div>
                        </div>
                    </div>
                </article>
            `;
        }).join('');
    }

    function formatDate(value) {
        if (!value) {
            return '—';
        }

        return new Date(value).toLocaleString();
    }
})();
