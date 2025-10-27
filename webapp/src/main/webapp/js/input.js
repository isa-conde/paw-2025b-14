function updateFileName(inputId, textId) {
    const input = document.getElementById(inputId);
    const textElement = document.getElementById(textId);

    if (input.files && input.files[0]) {
        textElement.textContent = input.files[0].name;
    } else {
        textElement.textContent = 'Upload image';
    }
}

function onlyUnsignedIntKeydown(e) {
    const k = e.key;
    const ctrl = e.ctrlKey || e.metaKey;

    const allowed = ['Backspace','Delete','ArrowLeft','ArrowRight','Tab','Home','End'];
    if (allowed.includes(k) || (ctrl && ['a','c','v','x'].includes(k.toLowerCase()))) return true;

    if (k >= '0' && k <= '9') return true;

    e.preventDefault();
    return false;
}

function onlyUnsignedIntPaste(e) {
    const dt = e.clipboardData || window.clipboardData;
    if (!dt) return true;
    const digits = dt.getData('text').replace(/\D+/g, '');
    if (digits === '') {
        e.preventDefault();
        return false;
    }
    e.preventDefault();
    const el = e.target;
    const start = el.selectionStart, end = el.selectionEnd;
    const val = el.value;
    el.value = val.slice(0, start) + digits + val.slice(end);
    const pos = start + digits.length;
    el.setSelectionRange(pos, pos);
    return false;
}
