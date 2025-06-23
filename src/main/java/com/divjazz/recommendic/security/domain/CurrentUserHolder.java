package com.divjazz.recommendic.security.domain;

import com.divjazz.recommendic.user.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class CurrentUserHolder {
    private User user;
}
