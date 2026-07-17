package ar.edu.itba.paw.webapp.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class StaticCacheFilter implements Filter {

    private static final String INDEX_HTML = "/index.html";
    private static final String NO_STORE = "no-store, no-cache, must-revalidate, max-age=0";
    private static final String REVALIDATE = "no-cache, must-revalidate";
    private static final String SHORT_PUBLIC_CACHE = "public, max-age=3600";
    private static final String IMMUTABLE_CACHE = "public, max-age=31536000, immutable";
    private static final Pattern FILE_EXTENSION = Pattern.compile(".*/?[^/]+\\.[^/]+$");
    private static final Pattern VITE_HASHED_ASSET = Pattern.compile(".+-[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9]+$");

    @Override
    public void init(FilterConfig filterConfig) {
        // No-op.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = pathWithinApplication(httpRequest);

        applyCachePolicy(path, httpRequest, httpResponse);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No-op.
    }

    private void applyCachePolicy(String path, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (isSpaShell(path)) {
            noStore(response);
            return;
        }

        if (isPublicStaticAsset(path) && !resourceExists(request, path)) {
            return;
        }

        if (isHashedViteAsset(path)) {
            response.setHeader("Cache-Control", IMMUTABLE_CACHE);
            return;
        }

        if (path.startsWith("/locales/")) {
            response.setHeader("Cache-Control", REVALIDATE);
            return;
        }

        if (isPublicStaticAsset(path)) {
            response.setHeader("Cache-Control", SHORT_PUBLIC_CACHE);
        }
    }

    private boolean isSpaShell(String path) {
        return "/".equals(path) || INDEX_HTML.equals(path) || (!isApiPath(path) && !hasFileExtension(path));
    }

    private boolean isApiPath(String path) {
        return "/api".equals(path) || path.startsWith("/api/");
    }

    private boolean hasFileExtension(String path) {
        return FILE_EXTENSION.matcher(path).matches();
    }

    private boolean isHashedViteAsset(String path) {
        if (!path.startsWith("/assets/")) {
            return false;
        }
        String filename = path.substring(path.lastIndexOf('/') + 1);
        return VITE_HASHED_ASSET.matcher(filename).matches();
    }

    private boolean isPublicStaticAsset(String path) {
        return path.startsWith("/assets/")
                || path.startsWith("/locales/")
                || path.startsWith("/public/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/fonts/")
                || "/favicon.ico".equals(path)
                || "/vite.svg".equals(path);
    }

    private String pathWithinApplication(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestUri;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return path.isEmpty() ? "/" : path;
    }

    private boolean resourceExists(HttpServletRequest request, String path) throws IOException {
        return request.getServletContext().getResource(path) != null;
    }

    private void noStore(HttpServletResponse response) {
        response.setHeader("Cache-Control", NO_STORE);
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
