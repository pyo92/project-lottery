package com.example.projectlottery.config.handler;

import org.springframework.context.ApplicationEvent;

public class UserRoleChangeEvent extends ApplicationEvent {

    private final String userId;

    public UserRoleChangeEvent(Object source, String userId) {
        super(source);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
