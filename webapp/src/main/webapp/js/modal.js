function openModal(id) {
    const modal = findModal(id);
    modal.showModal();
}

function closeModal(id) {
    const modal = findModal(id)
    modal.close();
}

function findModal(id) {
    const modal = document.getElementById(id);
    if (!modal) {
        console.error(`Modal with id "${id}" not found.`);
        return;
    }
    return modal;
}