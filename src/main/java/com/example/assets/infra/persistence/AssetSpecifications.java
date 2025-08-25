package com.example.assets.infra.persistence;

import org.springframework.data.jpa.domain.Specification;
import java.time.Instant;

/**
 * Specifications for filtering AssetEntity in JPA queries.
 */
public final class AssetSpecifications {

    private AssetSpecifications() {
        //Utility class
    }

    public static Specification<AssetEntity> uploadDateStart(Instant start) {
        return (root, query, cb) ->
                start == null ? null : cb.greaterThanOrEqualTo(root.get("uploadDate"), start);
    }

    public static Specification<AssetEntity> uploadDateEnd(Instant end) {
        return (root, query, cb) ->
                end == null ? null : cb.lessThanOrEqualTo(root.get("uploadDate"), end);
    }

    public static Specification<AssetEntity> filenameLike(String pattern) {
        if (pattern == null) return null;
        String like = pattern.replace("*", "%");
        return (root, query, cb) -> cb.like(root.get("filename"), like);
    }

    public static Specification<AssetEntity> filetypeEq(String filetype) {
        return (root, query, cb) ->
                filetype == null ? null : cb.equal(root.get("contentType"), filetype);
    }
}
