package com.nonprofit.model.notification;

import com.nonprofit.model.people.Person;

/**
 * STRATEGY PATTERN - Strategy interface.
 * One delivery algorithm per communication channel.
 */
public interface NotificationStrategy {

    /** Delivers the message; returns false if this channel cannot reach the person. */
    boolean send(Person to, Message msg);
}
