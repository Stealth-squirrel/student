function logout() { clearToken(); window.location.href = `${PAGE_BASE}/login.html`; }
function requireAuth() { if (!getToken()) window.location.href = `${PAGE_BASE}/login.html`; }

if (location.pathname.endsWith('/login.html') && getToken()) {
    redirectByRole();
}

document.getElementById('login-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const errorEl = document.getElementById('error');
    errorEl.textContent = '';
    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email:    document.getElementById('email').value,
                password: document.getElementById('password').value
            })
        });
        if (!res.ok) throw new Error('Неверный email или пароль');
        const data = await res.json();
        setToken(data.token, data.roles);
        redirectByRole();
    } catch (err) {
        errorEl.textContent = err.message;
    }
});