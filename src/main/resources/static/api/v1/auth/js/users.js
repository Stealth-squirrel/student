requireAuth();
if (!getRoles().includes('ROLE_SUPER_ADMIN')) {
    redirectByRole();
}

document.getElementById('who').textContent = roleNames();

async function load() {
    const data = await apiFetch('/users');
    document.getElementById('counter').textContent = `· всего ${data.totalCount}`;
    document.getElementById('tbody').innerHTML = data.responses.map(u => `
        <tr>
            <td>${u.id}</td>
            <td>${u.email}</td>
            <td>${u.roles.map(r => `<span class="tag">${r}</span>`).join(' ')}</td>
            <td><button class="danger" onclick="del(${u.id})">Удалить</button></td>
        </tr>`).join('');
}

async function del(id) {
    if (!confirm(`Удалить пользователя ${id}?`)) return;
    try {
        await apiFetch(`/users/${id}`, { method: 'DELETE' });
        load();
    } catch (e) {
        document.getElementById('msg').textContent = e.message;
    }
}

document.getElementById('create-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    document.getElementById('msg').textContent = '';
    const body = {
        email:    document.getElementById('f-email').value,
        password: document.getElementById('f-pass').value,
        roles:    [document.getElementById('f-role').value]
    };
    try {
        await apiFetch('/users/save', { method: 'POST', body: JSON.stringify(body) });
        e.target.reset();
        load();
    } catch (err) {
        document.getElementById('msg').textContent = err.message;
    }
});

load().catch(e => document.getElementById('msg').textContent = e.message);