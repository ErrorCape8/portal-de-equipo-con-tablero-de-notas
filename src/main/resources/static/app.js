const token = () => localStorage.getItem('jwt');
const currentUser = () => JSON.parse(localStorage.getItem('user') || 'null');

function requireSession() {
    if (!token()) {
        window.location.href = '/login.html';
        return false;
    }
    const user = currentUser();
    document.querySelectorAll('[data-user-name]').forEach((element) => element.textContent = user?.name || user?.email || 'Usuario');
    document.querySelectorAll('[data-user-role]').forEach((element) => element.textContent = user?.role || 'USER');
    document.querySelectorAll('[data-admin-only]').forEach((element) => {
        if (user?.role !== 'ADMIN') element.remove();
        else element.classList.remove('hidden');
    });
    return true;
}

async function api(path, options = {}) {
    const headers = { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...(options.headers || {}) };
    if (token()) headers.Authorization = `Bearer ${token()}`;
    const response = await fetch(path, { ...options, headers });
    if (response.status === 401) {
        localStorage.removeItem('jwt');
        localStorage.removeItem('user');
        window.location.href = '/login.html';
        throw new Error('La sesión expiró.');
    }
    const text = await response.text();
    let data = null;
    try { data = text ? JSON.parse(text) : null; } catch { data = text; }
    if (!response.ok) throw new Error(data?.message || 'No se pudo completar la operación.');
    return data;
}

function logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    window.location.href = '/login.html';
}

function statusLabel(status) {
    return ({ PENDIENTE: 'Pendiente', EN_PROCESO: 'En proceso', COMPLETADO: 'Completada' })[status] || status;
}

function statusClass(status) {
    return ({ PENDIENTE: 'pending', EN_PROCESO: 'progress', COMPLETADO: 'done' })[status] || '';
}

function showToast(message, error = false) {
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.textContent = message;
    if (error) toast.style.background = '#b94d35';
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3200);
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.body.dataset.authenticated === 'true') requireSession();
    document.querySelectorAll('[data-logout]').forEach((button) => button.addEventListener('click', logout));
});
