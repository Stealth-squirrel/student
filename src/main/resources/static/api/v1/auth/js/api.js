const API_BASE  = '/api/v1';
const PAGE_BASE = '/api/v1/auth';

const getToken   = () => localStorage.getItem('token');
const getRoles   = () => { try { return JSON.parse(localStorage.getItem('roles') || '[]'); } catch { return []; } };
const setToken   = (token, roles) => { localStorage.setItem('token', token); localStorage.setItem('roles', JSON.stringify(roles)); };
const clearToken = () => { localStorage.removeItem('token'); localStorage.removeItem('roles'); };
const roleNames  = () => getRoles().map(r => r.replace('ROLE_', '')).join(', ');

function redirectByRole() {
    const roles = getRoles();
    if (roles.includes('ROLE_SUPER_ADMIN')) {
        window.location.href = `${PAGE_BASE}/users.html`;
    } else if (roles.includes('ROLE_ADMIN') || roles.includes('ROLE_TEACHER')) {
        window.location.href = `${PAGE_BASE}/students.html`;
    } else {
        clearToken();
        window.location.href = `${PAGE_BASE}/login.html`;
    }
}

async function apiFetch(path, options = {}) {
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    const token = getToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

    if (res.status === 401) {
        clearToken();
        window.location.href = `${PAGE_BASE}/login.html`;
        throw new Error('Требуется вход в систему');
    }
    if (res.status === 403) {
        throw new Error('Недостаточно прав для выполнения этого действия');
    }
    if (!res.ok) {
        const err = await res.json().catch(() => ({ message: `Ошибка ${res.status}` }));
        throw new Error(err.message || `Ошибка ${res.status}`);
    }
    const text = await res.text();
    return text ? JSON.parse(text) : null;
}