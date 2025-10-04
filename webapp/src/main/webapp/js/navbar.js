function switchNavbar(paramName, section) {
    const currentUrl = new URL(window.location);
    currentUrl.searchParams.set(paramName, section);
    window.location.href = currentUrl.toString();
}