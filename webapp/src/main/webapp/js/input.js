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

function onlyUnsignedDecimalKeydown(e) {
    const allowed = ['Backspace','Delete','Tab','Escape','Enter','ArrowLeft','ArrowRight','ArrowUp','ArrowDown','Home','End'];
    if (allowed.includes(e.key) || e.ctrlKey || e.metaKey) return true;

    if (e.key === '.' || e.key === ',') {
        if (e.target.value.includes('.')) { e.preventDefault(); return false; }
        return true;
    }
    if (/^\d$/.test(e.key)) return true;

    e.preventDefault();
    return false;
}

function onlyUnsignedDecimalPaste(e) {
    const text = (e.clipboardData || window.clipboardData).getData('text').replace(',', '.');
    if (!/^\d*(\.\d*)?$/.test(text)) { e.preventDefault(); return false; }
    return true;
}

function clampDecimalInput(el, min, max, step) {
    let v = el.value.replace(',', '.').replace(/[^0-9.]/g, '');
    const dot = v.indexOf('.');
    if (dot !== -1) v = v.slice(0, dot + 1) + v.slice(dot + 1).replace(/\./g, '');

    if (v === '' || v === '.') return;

    let num = parseFloat(v);
    if (Number.isNaN(num)) { el.value = ''; return; }

    if (max != null && num > max) num = max;
    if (min != null && num < min) num = min;

    if (step && step > 0) {
        num = Math.round(num / step) * step;
        if (max != null && num > max) num = max;
        if (min != null && num < min) num = min;
    }

    el.value = Number(num.toFixed(2)).toString();
}
