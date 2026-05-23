package com.divjazz.recommendic.user.mapper;

import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.controller.UserPrincipalResponse;
import com.divjazz.recommendic.user.controller.UserResponse;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserMapper {

    UserDTO toUserDTO(User user);

    @Mapping(target = "userPrincipal", source = "userPrincipal",
            qualifiedByName = "stripPrincipal")
    UserResponse toUserResponse(User user);

    @Named("stripPrincipal")
    default UserPrincipalResponse stripUserPrincipal(UserPrincipal userPrincipal) {

        return new UserPrincipalResponse(userPrincipal.getEmail(),
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
                userPrincipal.getRole());
    }
}
