package com.divjazz.recommendic.user.event;

import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.model.User;

import java.util.Map;

public class UserEvent {

    private User user;
    private EventType eventType;
    private Map<?, ?> data;

    public UserEvent(User user, EventType eventType, Map<?, ?> data) {
        this.user = user;
        this.eventType = eventType;
        this.data = data;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Map<?, ?> getData() {
        return data;
    }

    public void setData(Map<?, ?> data) {
        this.data = data;
    }
}
