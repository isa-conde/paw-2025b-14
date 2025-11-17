window.carrouselState = window.carrouselState || {};

function initCarrousel(el) {
    const id = el.id;
    const track = el.querySelector('.carrousel-track');
    const itemsPerView = parseInt(el.dataset.itemsPerView, 10) || 3;
    const totalItems = track ? track.children.length : 0;

    if (!carrouselState[id]) {
        carrouselState[id] = { currentIndex: 0, itemsPerView, totalItems };
    }
}

function moveCarrousel(id, direction) {
    const state = carrouselState[id];
    const maxIndex = state.totalItems;

    state.currentIndex += (direction * state.itemsPerView);
    state.currentIndex = (maxIndex + state.currentIndex) % maxIndex;
    while (state.currentIndex % state.itemsPerView !== 0) {
        state.currentIndex -= direction;
    }

    const carrousel = document.getElementById(id);
    const track = carrousel.querySelector('.carrousel-track');

    const items = carrousel.querySelectorAll('.carrousel-item');
    let step;
    if (items.length >= 2) {
        step = items[1].getBoundingClientRect().left - items[0].getBoundingClientRect().left;
    } else if (items.length === 1) {
        const it = items[0];
        const w = it.getBoundingClientRect().width;
        const cs = getComputedStyle(it);
        step = w + (parseFloat(cs.marginLeft) || 0) + (parseFloat(cs.marginRight) || 0);
    } else {
        step = 0;
    }

    track.style.transition = 'transform 0.3s ease';
    track.style.transform = 'translateX(' + (-(state.currentIndex * step)) + 'px)';
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.carrousel').forEach(initCarrousel);
});
