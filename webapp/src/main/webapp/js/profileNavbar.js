function switchProfileNavbar(profileId, section) {
    if (section === 'overview') {
        window.location.href = `/profile/${profileId}`;
    } else if (section === 'tournaments') {
        window.location.href = `/profile/${profileId}/tournaments`;
    }
}