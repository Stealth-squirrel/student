requireAuth();
const isAdmin   = getRoles().includes('ROLE_ADMIN');
const isTeacher = getRoles().includes('ROLE_TEACHER');

if (!isAdmin && !isTeacher) {
    redirectByRole();
}

document.getElementById('who').textContent = roleNames();

if (isAdmin) {
    document.getElementById('msg').insertAdjacentHTML('beforebegin', `
        <form id="create-form" class="card row">
            <input id="f-name"    placeholder="Имя"     required>
            <input id="f-surname" placeholder="Фамилия" required>
            <input id="f-phone"   placeholder="Телефон" required>
            <input id="f-email"   type="email" placeholder="Email" required>
            <button type="submit">Добавить</button>
        </form>
    `);
    document.getElementById('create-form').addEventListener('submit', onSubmit);
}

async function load() {
    const data = await apiFetch('/student');
    document.getElementById('counter').textContent = `· всего ${data.totalCount}`;
    document.getElementById('tbody').innerHTML = data.response.map(s => `
        <tr>
            <td>${s.id}</td>
            <td>${s.name}</td>
            <td>${s.surname}</td>
            <td>
                <button class="secondary" onclick="showFull(${s.id})">Подробнее</button>
                ${isAdmin ? `
                    <button class="secondary" onclick="edit(${s.id})">Изменить</button>
                    <button class="danger"    onclick="del(${s.id})">Удалить</button>
                ` : ''}
            </td>
        </tr>`).join('');
}

async function showFull(id) {
    try {
        const full = await apiFetch(`/student/${id}`);
        document.getElementById('modal-body').innerHTML = `
            <dt>ID</dt><dd>${full.id}</dd>
            <dt>Имя</dt><dd>${full.name ?? '—'}</dd>
            <dt>Фамилия</dt><dd>${full.surname ?? '—'}</dd>
            <dt>Телефон</dt><dd>${full.phoneNumber ?? '—'}</dd>
            <dt>Email</dt><dd>${full.email ?? '—'}</dd>
        `;
        document.getElementById('modal').hidden = false;
    } catch (e) {
        document.getElementById('msg').textContent = e.message;
    }
}

function closeModal() {
    document.getElementById('modal').hidden = true;
}

async function edit(id) {
    try {
        const full = await apiFetch(`/student/${id}`);
        const phoneNumber = prompt('Телефон:', full.phoneNumber || '');
        if (phoneNumber === null) return;
        const email = prompt('Email:', full.email || '');
        if (email === null) return;
        await apiFetch(`/student/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ phoneNumber, email })
        });
        load();
    } catch (e) {
        document.getElementById('msg').textContent = e.message;
    }
}

async function del(id) {
    if (!confirm(`Удалить студента ${id}?`)) return;
    try {
        await apiFetch(`/student/${id}`, { method: 'DELETE' });
        load();
    } catch (e) {
        document.getElementById('msg').textContent = e.message;
    }
}

async function onSubmit(e) {
    e.preventDefault();
    document.getElementById('msg').textContent = '';
    const body = {
        name:         document.getElementById('f-name').value,
        surname:      document.getElementById('f-surname').value,
        phone_number: document.getElementById('f-phone').value,
        email:        document.getElementById('f-email').value
    };
    try {
        await apiFetch('/student/save', { method: 'POST', body: JSON.stringify(body) });
        e.target.reset();
        load();
    } catch (err) {
        document.getElementById('msg').textContent = err.message;
    }
}

load().catch(e => document.getElementById('msg').textContent = e.message);