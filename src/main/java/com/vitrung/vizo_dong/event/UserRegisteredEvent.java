package com.vitrung.vizo_dong.event;

import com.vitrung.vizo_dong.entity.User;

import java.util.Locale;

public class UserRegisteredEvent {
    private final User user;
    private final Locale locale;

    public UserRegisteredEvent(User user, Locale locale) {
        this.user = user;
        this.locale = locale;
    }

    public User getUser() {
        return user;
    }

    public Locale getLocale() {
        return locale;
    }
}
