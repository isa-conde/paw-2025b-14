document.addEventListener("click", function (e) {
    const btn = e.target.closest(".copy-btn");
    if (!btn) return;

    const box = btn.closest(".copy-field-box");
    const text = box.getAttribute("data-value");

    navigator.clipboard.writeText(text).then(() => {
        btn.classList.add("copied");
        const original = btn.innerHTML;
        btn.innerHTML = "✓";

        setTimeout(() => {
            btn.innerHTML = original;
            btn.classList.remove("copied");
        }, 1000);
    });
});