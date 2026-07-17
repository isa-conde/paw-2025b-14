package ar.edu.itba.paw.webapp.restcontrollers;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class BinaryResponseSupport {

    private static final ConcurrentMap<String, ResourceVersion> RESOURCE_VERSIONS = new ConcurrentHashMap<>();

    private BinaryResponseSupport() {
    }

    static Response conditionalOk(Request request, byte[] body, String mediaType, String etagNamespace) {
        EntityTag entityTag = new EntityTag(etagNamespace + "-" + body.length + "-" + sha256(body));
        Date lastModified = lastModifiedFor(etagNamespace, entityTag);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMustRevalidate(true);
        cacheControl.setMaxAge(0);

        Response.ResponseBuilder preconditions = request.evaluatePreconditions(lastModified, entityTag);
        if (preconditions != null) {
            return preconditions
                    .lastModified(lastModified)
                    .tag(entityTag)
                    .cacheControl(cacheControl)
                    .build();
        }

        return Response.ok(body)
                .type(mediaType)
                .lastModified(lastModified)
                .tag(entityTag)
                .cacheControl(cacheControl)
                .header("Content-Length", body.length)
                .build();
    }

    private static String sha256(byte[] body) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest.digest(body));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 digest is not available", e);
        }
    }

    private static Date lastModifiedFor(String etagNamespace, EntityTag entityTag) {
        ResourceVersion version = RESOURCE_VERSIONS.compute(etagNamespace, (key, current) -> {
            String currentTag = entityTag.getValue();
            if (current != null && current.entityTag.equals(currentTag)) {
                return current;
            }

            long now = truncateToSecond(System.currentTimeMillis());
            long lastModified = current == null ? now : Math.max(now, current.lastModified + 1000);
            return new ResourceVersion(currentTag, lastModified);
        });
        return new Date(version.lastModified);
    }

    private static long truncateToSecond(long millis) {
        return (millis / 1000) * 1000;
    }

    private static final class ResourceVersion {
        private final String entityTag;
        private final long lastModified;

        private ResourceVersion(String entityTag, long lastModified) {
            this.entityTag = entityTag;
            this.lastModified = lastModified;
        }
    }
}
