package com.divjazz.recommendic.user.event;

import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private UserType userType;
    private EventType eventType;
    private Map<?, ?> data;
}
