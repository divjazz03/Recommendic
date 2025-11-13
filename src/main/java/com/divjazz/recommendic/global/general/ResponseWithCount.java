package com.divjazz.recommendic.global.general;

import java.util.Set;

public record ResponseWithCount<T>(Set<T> elements, int total) {
}
