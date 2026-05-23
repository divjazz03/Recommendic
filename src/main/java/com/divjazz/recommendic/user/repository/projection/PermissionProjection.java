package com.divjazz.recommendic.user.repository.projection;

import java.util.Set;

public record PermissionProjection(Long id,String scope, String resource, Set<String> actions) {
}
