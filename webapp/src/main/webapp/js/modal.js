(function () {
    function findModal(id) {
        const m = document.getElementById(id);
        if (!m) console.error('Modal not found:', id);
        return m;
    }

    window.openModal = function (id) {
        const m = findModal(id);
        if (!m) return;
        try {
            if (m.open) m.close();
            m.showModal();
        } catch {
            m.setAttribute('open', '');
        }
    };

    window.closeModal = function (id) {
        const m = findModal(id);
        if (m?.open) m.close();
    };

    document.addEventListener('DOMContentLoaded', () => {
        const f = document.getElementById('page-flags');
        if (f?.dataset.openEditModal === 'true') {
            openModal('editTournamentModal');
        }
    });

})();
