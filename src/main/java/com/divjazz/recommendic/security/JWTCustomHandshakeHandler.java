package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.exception.InvalidTokenException;
import com.divjazz.recommendic.security.exception.TokenNotFoundException;
import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class JWTCustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtService jwtService;
    private final GeneralUserService userService;

    public JWTCustomHandshakeHandler(JwtService jwtService, GeneralUserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String jwtToken = ((ServletServerHttpRequest) request).getServletRequest().getHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
            if (jwtService.validateToken(((ServletServerHttpRequest) request).getServletRequest())) {
                String username = jwtService.getClaimsValue(jwtToken, Claims::getSubject);
                String userId = userService.retrieveUserByEmail(username).getUserId();
                return () -> userId;
            }
            throw new InvalidTokenException();

        }
            throw new TokenNotFoundException();
    }
}
