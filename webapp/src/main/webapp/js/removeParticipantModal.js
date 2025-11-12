function openRemoveParticipantModal(id, name) {
    document.getElementById('modalParticipantId').value = id;
    document.getElementById('removeParticipantName').textContent = name;
    if (typeof openModal === 'function') {
    openModal('removeParticipantModal');
    } else {
        document.getElementById('removeParticipantModal')?.classList.add('open');
    }
}