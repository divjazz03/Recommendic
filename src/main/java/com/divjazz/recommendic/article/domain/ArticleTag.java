package com.divjazz.recommendic.article.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum ArticleTag {

    CHILD("child health"),
    HEART("heart"),
    CANCER("cancer"),
    SKIN("skin care"),
    BONE_AND_JOINT("bone and joint"),
    BRAIN("brain"),

    PRENATAL("pre natal"),
    POSTNATAL("post natal"),
    MENTAL("mental health"),
    ORAL("oral health"),
    EYE("eye care"),
    PHYSICAL_THERAPY("physical therapy");

    @JsonValue
    private final String value;

    ArticleTag(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ArticleTag fromValue(String value) throws IllegalArgumentException {
        if (Objects.nonNull(value)) {
            for (ArticleTag articleTag : values()) {
                if (articleTag.getValue().equalsIgnoreCase(value)) {
                    return articleTag;
                }
            }
            throw new IllegalArgumentException("Invalid article tag %s".formatted(value));
        }
        throw new IllegalArgumentException("Tag can't be null");
    }
}
