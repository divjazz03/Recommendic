package com.divjazz.recommendic.article.dto;

import com.divjazz.recommendic.user.model.User;

public record ArticleDTO(String title,String subtitle, String content, String[] tags) {

}
