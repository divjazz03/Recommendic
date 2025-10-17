package com.divjazz.recommendic.security.domain;

import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class CurrentUserHolder {
    private UserDTO user;
}
