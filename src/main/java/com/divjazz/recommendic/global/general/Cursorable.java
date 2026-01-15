package com.divjazz.recommendic.global.general;

import java.time.Instant;

public interface Cursorable {
    Instant cursorCreatedAt();
    Long cursorId();
}
