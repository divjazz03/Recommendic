package com.divjazz.recommendic.article.domain;

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

    private String value;

    ArticleTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
