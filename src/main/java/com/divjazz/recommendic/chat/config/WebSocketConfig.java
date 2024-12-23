package com.divjazz.recommendic.chat.config;

import com.divjazz.recommendic.security.JWTCustomHandshakeHandler;
import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final GeneralUserService userService;
    private final JwtService jwtService;

    public WebSocketConfig(GeneralUserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setHandshakeHandler(jwtCustomHandshakeHandler(userService,jwtService))
                .setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public JWTCustomHandshakeHandler jwtCustomHandshakeHandler(GeneralUserService userService, JwtService jwtService) {
        return new JWTCustomHandshakeHandler(jwtService, userService);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

}
