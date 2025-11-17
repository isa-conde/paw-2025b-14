(function initEditChecks(){
    document.addEventListener('change', function(e){
        if (!e.target.classList.contains('edit-check')) return;
        const checked = [...document.querySelectorAll('.edit-check:checked')];
        if (checked.length > 2) {
            e.target.checked = false;
        }
        updateSwapButton();
    });
    document.addEventListener('DOMContentLoaded', updateSwapButton);
})();
function updateSwapButton(){
    const btn = document.getElementById('swapBtn');
    if (!btn) return;
    const selected = [...document.querySelectorAll('.edit-check:checked')];

    btn.disabled = !(selected.length === 2);
}