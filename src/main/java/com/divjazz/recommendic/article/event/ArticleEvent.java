package com.divjazz.recommendic.article.event;

import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@AllArgsConstructor
@Setter
public class ArticleEvent {

    private UserDTO user;

    private EventType eventType;

    private Article article;
}
