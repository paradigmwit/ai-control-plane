(function () {
    function safeJsonParse(value) {
        if (typeof value !== 'string') {
            return value;
        }

        try {
            return JSON.parse(value);
        } catch (error) {
            return null;
        }
    }

    function formatJson(value) {
        if (value === null || value === undefined || value === '') {
            return '—';
        }

        if (typeof value === 'string') {
            const parsed = safeJsonParse(value);
            if (parsed !== null) {
                return JSON.stringify(parsed, null, 2);
            }
            return value;
        }

        return JSON.stringify(value, null, 2);
    }

    async function requestJson(url, options) {
        const response = await fetch(url, options);
        const text = await response.text();
        const data = text ? safeJsonParse(text) ?? text : null;
        return {
            ok: response.ok,
            status: response.status,
            data,
            text,
            url
        };
    }

    function setStatus(element, label, state) {
        if (!element) {
            return;
        }

        element.textContent = label;
        if (state) {
            element.dataset.state = state;
        } else {
            delete element.dataset.state;
        }
    }

    window.appShared = {
        formatJson,
        requestJson,
        safeJsonParse,
        setStatus
    };
})();
