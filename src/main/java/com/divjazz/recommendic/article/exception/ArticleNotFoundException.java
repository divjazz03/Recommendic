package com.divjazz.recommendic.article.exception;

public class ArticleNotFoundException extends RuntimeException{
    public static final String MESSAGE = "Article either doesn't exist or has been deleted";

    public ArticleNotFoundException() {
        super(MESSAGE);
    }
}
