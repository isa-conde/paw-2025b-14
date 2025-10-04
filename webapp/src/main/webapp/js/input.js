function updateFileName(inputId, textId) {
    const input = document.getElementById(inputId);
    const textElement = document.getElementById(textId);

    if (input.files && input.files[0]) {
        textElement.textContent = input.files[0].name;
    } else {
        textElement.textContent = 'Upload image';
    }
}