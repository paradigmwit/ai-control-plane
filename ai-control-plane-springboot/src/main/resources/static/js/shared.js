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
        const response = await fetch(url, Object.assign({ credentials: 'same-origin' }, options || {}));
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

    async function getSession() {
        const response = await requestJson('/api/auth/session');
        return response.ok ? response.data : { authenticated: false, roles: [] };
    }

    async function login(username, password, rememberMe) {
        const payload = new URLSearchParams({ username, password });
        if (rememberMe) {
            payload.set('rememberMe', 'true');
        }

        return requestJson('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: payload
        });
    }

    async function logout() {
        return requestJson('/api/auth/logout', { method: 'POST' });
    }

    function hasRole(session, role) {
        return Array.isArray(session?.roles) && session.roles.includes(role);
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
        getSession,
        hasRole,
        login,
        logout,
        requestJson,
        safeJsonParse,
        setStatus
    };
})();
