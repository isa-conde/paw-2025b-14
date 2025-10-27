function switchNavbar(paramName, section) {
    const currentUrl = new URL(window.location);
    currentUrl.searchParams.set(paramName, section);
    currentUrl.searchParams.delete("page");
    currentUrl.searchParams.delete("page2");
    currentUrl.searchParams.delete("page1");
    window.location.href = currentUrl.toString();
}