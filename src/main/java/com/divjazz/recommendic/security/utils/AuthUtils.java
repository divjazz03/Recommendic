package com.divjazz.recommendic.security.utils;

import com.divjazz.recommendic.security.SessionUser;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.security.domain.CurrentUserHolder;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.mapper.UserMapper;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import com.divjazz.recommendic.user.service.GeneralUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final CurrentUserHolder userHolder;
    private final UserMapper userMapper;
    private final GeneralUserService generalUserService;

    public UserDTO getCurrentUser() {
        if (Objects.isNull(userHolder.getUser())) {
            var authenticationToken = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) authenticationToken.getPrincipal();
            User user = generalUserService.retrieveUserByEmail(jwt.getClaimAsString("sub"));
            return userMapper.toUserDTO(user);
        }
        return userHolder.getUser();
    }
}
