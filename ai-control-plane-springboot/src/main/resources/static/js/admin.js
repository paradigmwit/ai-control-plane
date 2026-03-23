(function () {
    const { formatJson, getSession, hasRole, login, logout, requestJson, setStatus } = window.appShared;

    const endpointGroups = [
        {
            title: 'User APIs',
            endpoints: [
                { id: 'user-workflow-execute', name: 'Execute currency conversion workflow', method: 'POST', path: '/api/user/orchestration/workflows/currency-conversion/execute', body: { prompt: 'Convert 100 USD to EUR' } },
                { id: 'user-history', name: 'List currency conversion history', method: 'GET', path: '/api/user/orchestration/workflows/currency-conversion/history' },
                { id: 'user-plan-create', name: 'Create user plan', method: 'POST', path: '/api/user/orchestration/plan', body: { taskSummary: 'Plan a multi-step task', metadata: 'manual request from admin console' } },
                { id: 'user-plan-execute', name: 'Execute user plan', method: 'POST', path: '/api/user/orchestration/execute/{planId}', pathParams: [{ key: 'planId', value: 'workflow.currency-conversion.v1' }] },
                { id: 'user-execution-status', name: 'Get execution status', method: 'GET', path: '/api/user/orchestration/executions/{instanceId}', pathParams: [{ key: 'instanceId', value: 'instance-123' }] },
                { id: 'user-execution-steps', name: 'List execution steps', method: 'GET', path: '/api/user/orchestration/executions/{instanceId}/steps', pathParams: [{ key: 'instanceId', value: 'instance-123' }] },
                { id: 'user-step-complete', name: 'Mark step complete', method: 'POST', path: '/api/user/orchestration/executions/{instanceId}/steps/{stepId}/complete', pathParams: [{ key: 'instanceId', value: 'instance-123' }, { key: 'stepId', value: 'currency-conversion.convert-amount' }] },
                { id: 'user-profiles', name: 'List user profiles', method: 'GET', path: '/api/user/profiles' }
            ]
        },
        {
            title: 'Admin APIs',
            endpoints: [
                { id: 'admin-users', name: 'List admin users', method: 'GET', path: '/api/admin/users' },
                { id: 'admin-plans-create', name: 'Create plan', method: 'POST', path: '/api/admin/plans', body: { planId: 'plan-demo', metadata: '{"task":"demo"}', createdAt: '2026-01-01T00:00:00Z' } },
                { id: 'admin-plans-list', name: 'List plans', method: 'GET', path: '/api/admin/plans' },
                { id: 'admin-plan-get', name: 'Get plan', method: 'GET', path: '/api/admin/plans/{id}', pathParams: [{ key: 'id', value: 'plan-demo' }] },
                { id: 'admin-plan-update', name: 'Update plan', method: 'PUT', path: '/api/admin/plans/{id}', pathParams: [{ key: 'id', value: 'plan-demo' }], body: { planId: 'plan-demo', metadata: '{"task":"updated"}', createdAt: '2026-01-01T00:00:00Z' } },
                { id: 'admin-plan-delete', name: 'Delete plan', method: 'DELETE', path: '/api/admin/plans/{id}', pathParams: [{ key: 'id', value: 'plan-demo' }] },
                { id: 'admin-steps-create', name: 'Create step', method: 'POST', path: '/api/admin/steps', body: { stepId: 'step-demo', planId: 'plan-demo', toolName: 'tool.demo', inputPayload: '{"prompt":"hello"}', metadata: '{"priority":"low"}' } },
                { id: 'admin-steps-list', name: 'List steps', method: 'GET', path: '/api/admin/steps' },
                { id: 'admin-step-get', name: 'Get step', method: 'GET', path: '/api/admin/steps/{id}', pathParams: [{ key: 'id', value: 'step-demo' }] },
                { id: 'admin-step-update', name: 'Update step', method: 'PUT', path: '/api/admin/steps/{id}', pathParams: [{ key: 'id', value: 'step-demo' }], body: { stepId: 'step-demo', planId: 'plan-demo', toolName: 'tool.updated', inputPayload: '{"prompt":"updated"}', metadata: '{"priority":"high"}' } },
                { id: 'admin-step-delete', name: 'Delete step', method: 'DELETE', path: '/api/admin/steps/{id}', pathParams: [{ key: 'id', value: 'step-demo' }] },
                { id: 'admin-dependency-create', name: 'Create step dependency', method: 'POST', path: '/api/admin/step-dependencies', body: { stepId: 'step-demo', dependsOnStep: 'step-parent' } },
                { id: 'admin-dependencies-list', name: 'List step dependencies', method: 'GET', path: '/api/admin/step-dependencies' },
                { id: 'admin-dependency-get', name: 'Get step dependency', method: 'GET', path: '/api/admin/step-dependencies/{id}', pathParams: [{ key: 'id', value: 'step-demo' }] },
                { id: 'admin-dependency-update', name: 'Update step dependency', method: 'PUT', path: '/api/admin/step-dependencies/{id}', pathParams: [{ key: 'id', value: 'step-demo' }], body: { stepId: 'step-demo', dependsOnStep: 'step-parent-updated' } },
                { id: 'admin-dependency-delete', name: 'Delete step dependency', method: 'DELETE', path: '/api/admin/step-dependencies/{id}', pathParams: [{ key: 'id', value: 'step-demo' }] },
                { id: 'admin-instance-create', name: 'Create instance', method: 'POST', path: '/api/admin/instances', body: { instanceId: 'instance-demo', planId: 'plan-demo', status: 'RUNNING', createdAt: '2026-01-01T00:00:00Z', startedAt: '2026-01-01T00:00:00Z', completedAt: null, totalCost: 0, errorPayload: null } },
                { id: 'admin-instances-list', name: 'List instances', method: 'GET', path: '/api/admin/instances' },
                { id: 'admin-instance-get', name: 'Get instance details', method: 'GET', path: '/api/admin/instances/{id}', pathParams: [{ key: 'id', value: 'instance-demo' }] },
                { id: 'admin-instance-update', name: 'Update instance', method: 'PUT', path: '/api/admin/instances/{id}', pathParams: [{ key: 'id', value: 'instance-demo' }], body: { instanceId: 'instance-demo', planId: 'plan-demo', status: 'COMPLETED', createdAt: '2026-01-01T00:00:00Z', startedAt: '2026-01-01T00:00:00Z', completedAt: '2026-01-01T00:10:00Z', totalCost: 0, errorPayload: null } },
                { id: 'admin-instance-delete', name: 'Delete instance', method: 'DELETE', path: '/api/admin/instances/{id}', pathParams: [{ key: 'id', value: 'instance-demo' }] },
                { id: 'admin-step-execution-create', name: 'Create step execution', method: 'POST', path: '/api/admin/step-executions', body: { stepExecutionId: 'step-exec-demo', instanceId: 'instance-demo', stepId: 'step-demo', status: 'COMPLETED', outputPayload: '{"ok":true}', errorMessage: null, startedAt: '2026-01-01T00:00:00Z', completedAt: '2026-01-01T00:00:02Z', executionTimeMs: 2000, stepCost: 0 } },
                { id: 'admin-step-executions-list', name: 'List step executions', method: 'GET', path: '/api/admin/step-executions' },
                { id: 'admin-step-execution-get', name: 'Get step execution', method: 'GET', path: '/api/admin/step-executions/{id}', pathParams: [{ key: 'id', value: 'step-exec-demo' }] },
                { id: 'admin-step-execution-update', name: 'Update step execution', method: 'PUT', path: '/api/admin/step-executions/{id}', pathParams: [{ key: 'id', value: 'step-exec-demo' }], body: { stepExecutionId: 'step-exec-demo', instanceId: 'instance-demo', stepId: 'step-demo', status: 'FAILED', outputPayload: null, errorMessage: 'Manual update', startedAt: '2026-01-01T00:00:00Z', completedAt: '2026-01-01T00:00:02Z', executionTimeMs: 2000, stepCost: 0 } },
                { id: 'admin-step-execution-delete', name: 'Delete step execution', method: 'DELETE', path: '/api/admin/step-executions/{id}', pathParams: [{ key: 'id', value: 'step-exec-demo' }] },
                { id: 'admin-llm-create', name: 'Create LLM metadata', method: 'POST', path: '/api/admin/llm-metadata', body: { instanceId: 'instance-demo', providerId: 'local-ollama', modelName: 'llama3', promptTokens: 120, completionTokens: 42, llmCost: 0, rawResponse: '{"parsed":true}' } },
                { id: 'admin-llm-list', name: 'List LLM metadata', method: 'GET', path: '/api/admin/llm-metadata' },
                { id: 'admin-llm-get', name: 'Get LLM metadata', method: 'GET', path: '/api/admin/llm-metadata/{id}', pathParams: [{ key: 'id', value: 'instance-demo' }] },
                { id: 'admin-llm-update', name: 'Update LLM metadata', method: 'PUT', path: '/api/admin/llm-metadata/{id}', pathParams: [{ key: 'id', value: 'instance-demo' }], body: { instanceId: 'instance-demo', providerId: 'local-ollama', modelName: 'llama3.1', promptTokens: 140, completionTokens: 50, llmCost: 0, rawResponse: '{"parsed":true,"updated":true}' } },
                { id: 'admin-llm-delete', name: 'Delete LLM metadata', method: 'DELETE', path: '/api/admin/llm-metadata/{id}', pathParams: [{ key: 'id', value: 'instance-demo' }] }
            ]
        }
    ];

    const authForm = document.getElementById('auth-form');
    const authSignedOut = document.getElementById('auth-signed-out');
    const authSignedIn = document.getElementById('auth-signed-in');
    const authUsername = document.getElementById('auth-username');
    const authPassword = document.getElementById('auth-password');
    const authRemember = document.getElementById('auth-remember');
    const authUsernameDisplay = document.getElementById('auth-username-display');
    const authRoleList = document.getElementById('auth-role-list');
    const authFeedback = document.getElementById('auth-feedback');
    const logoutButton = document.getElementById('logout-button');
    const accessPanel = document.getElementById('access-panel');
    const accessTitle = document.getElementById('access-title');
    const accessCopy = document.getElementById('access-copy');
    const pageContent = document.getElementById('page-content');

    const groupsContainer = document.getElementById('admin-groups');
    const adminForm = document.getElementById('admin-form');
    const requestEmpty = document.getElementById('request-empty');
    const requestTitle = document.getElementById('request-title');
    const pathFields = document.getElementById('path-fields');
    const requestBody = document.getElementById('request-body');
    const resetButton = document.getElementById('reset-request');
    const responseStatus = document.getElementById('response-status');
    const responseMethod = document.getElementById('response-method');
    const responsePath = document.getElementById('response-path');
    const responsePanelBody = document.getElementById('response-body');
    const statusPill = document.getElementById('admin-status');

    let activeEndpoint = null;
    let session = { authenticated: false, roles: [] };

    renderEndpointGroups();
    selectEndpoint(endpointGroups[0].endpoints[0]);

    authForm.addEventListener('submit', async function (event) {
        event.preventDefault();
        authFeedback.textContent = 'Signing in…';

        const response = await login(authUsername.value.trim(), authPassword.value, authRemember.checked);
        if (!response.ok) {
            authFeedback.textContent = response.data?.message || 'Unable to sign in.';
            authPassword.value = '';
            return;
        }

        authForm.reset();
        authFeedback.textContent = 'Signed in.';
        await bootstrapSession(response.data);
    });

    logoutButton.addEventListener('click', async function () {
        await logout();
        authFeedback.textContent = 'Signed out.';
        await bootstrapSession({ authenticated: false, roles: [] });
    });

    adminForm.addEventListener('submit', async function (event) {
        event.preventDefault();
        if (!activeEndpoint || !hasRole(session, 'ROLE_ADMIN')) {
            return;
        }

        const resolvedPath = buildPath(activeEndpoint);
        const options = { method: activeEndpoint.method, headers: {} };
        const rawBody = requestBody.value.trim();

        if (['POST', 'PUT'].includes(activeEndpoint.method) && rawBody) {
            try {
                options.body = JSON.stringify(JSON.parse(rawBody));
                options.headers['Content-Type'] = 'application/json';
            } catch (error) {
                setStatus(statusPill, 'Invalid JSON body', 'error');
                responsePanelBody.textContent = 'Request body must be valid JSON.';
                return;
            }
        }

        setStatus(statusPill, 'Sending…', 'loading');

        const result = await requestJson(resolvedPath, options);
        responseStatus.textContent = String(result.status);
        responseMethod.textContent = activeEndpoint.method;
        responsePath.textContent = resolvedPath;
        responsePanelBody.textContent = formatJson(result.data || result.text);
        setStatus(statusPill, result.ok ? 'Completed' : 'Request failed', result.ok ? 'success' : 'error');
    });

    resetButton.addEventListener('click', function () {
        if (activeEndpoint) {
            selectEndpoint(activeEndpoint);
        }
    });

    bootstrapSession();

    async function bootstrapSession(initialSession) {
        session = initialSession || await getSession();
        const isAdmin = hasRole(session, 'ROLE_ADMIN');

        authSignedOut.classList.toggle('hidden', Boolean(session.authenticated));
        authSignedIn.classList.toggle('hidden', !session.authenticated);
        pageContent.classList.toggle('hidden', !isAdmin);
        accessPanel.classList.toggle('hidden', isAdmin);

        if (session.authenticated) {
            authUsernameDisplay.textContent = session.username || 'Signed in';
            authRoleList.innerHTML = (session.roles || []).map(function (role) {
                return `<span class="badge">${role.replace('ROLE_', '')}</span>`;
            }).join('');
        } else {
            authRoleList.innerHTML = '';
        }

        if (isAdmin) {
            accessTitle.textContent = 'Signed in';
            accessCopy.textContent = 'Your admin session is active. Use the admin console below.';
        } else if (session.authenticated) {
            accessTitle.textContent = 'Admin role required';
            accessCopy.textContent = 'Your account is signed in, but only admins can access the admin console.';
        } else {
            accessTitle.textContent = 'Admin session required';
            accessCopy.textContent = 'Sign in with an admin account to access role-protected admin APIs and the admin request console.';
        }
    }

    function renderEndpointGroups() {
        groupsContainer.innerHTML = endpointGroups.map(function (group) {
            return `
                <section class="endpoint-group">
                    <div>
                        <p class="eyebrow">${group.title}</p>
                    </div>
                    ${group.endpoints.map(function (endpoint) {
                        return `
                            <button type="button" class="endpoint-card" data-endpoint-id="${endpoint.id}">
                                <div class="endpoint-method">${endpoint.method}</div>
                                <strong>${endpoint.name}</strong>
                                <p class="subtle">${endpoint.path}</p>
                            </button>
                        `;
                    }).join('')}
                </section>
            `;
        }).join('');

        groupsContainer.querySelectorAll('[data-endpoint-id]').forEach(function (button) {
            button.addEventListener('click', function () {
                const endpointId = button.dataset.endpointId;
                const endpoint = endpointGroups
                    .flatMap(function (group) { return group.endpoints; })
                    .find(function (item) { return item.id === endpointId; });
                selectEndpoint(endpoint);
            });
        });
    }

    function selectEndpoint(endpoint) {
        activeEndpoint = endpoint;
        groupsContainer.querySelectorAll('[data-endpoint-id]').forEach(function (candidate) {
            candidate.classList.toggle('active', candidate.dataset.endpointId === endpoint.id);
        });
        requestTitle.textContent = `${endpoint.method} ${endpoint.path}`;
        requestEmpty.classList.add('hidden');
        adminForm.classList.remove('hidden');
        pathFields.innerHTML = (endpoint.pathParams || []).map(function (param) {
            return `
                <div>
                    <label for="param-${param.key}">${param.key}</label>
                    <input id="param-${param.key}" name="${param.key}" value="${param.value || ''}">
                </div>
            `;
        }).join('');
        requestBody.value = endpoint.body ? JSON.stringify(endpoint.body, null, 2) : '';
        requestBody.disabled = !['POST', 'PUT'].includes(endpoint.method);
        responseMethod.textContent = endpoint.method;
        responsePath.textContent = endpoint.path;
        responseStatus.textContent = '—';
        responsePanelBody.textContent = 'Ready to send request.';
        setStatus(statusPill, 'Ready', null);
    }

    function buildPath(endpoint) {
        return (endpoint.pathParams || []).reduce(function (currentPath, param) {
            const field = document.getElementById(`param-${param.key}`);
            return currentPath.replace(`{${param.key}}`, encodeURIComponent(field.value.trim()));
        }, endpoint.path);
    }
})();
