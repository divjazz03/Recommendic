package com.divjazz.recommendic.article.event;

import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@AllArgsConstructor
@Setter
public class ArticleEvent {

    private User user;

    private EventType eventType;

    private Article article;
}
