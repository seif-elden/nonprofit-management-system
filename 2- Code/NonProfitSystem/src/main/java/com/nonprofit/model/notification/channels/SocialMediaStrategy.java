package com.nonprofit.model.notification.channels;

import com.nonprofit.model.notification.Message;
import com.nonprofit.model.notification.NotificationStrategy;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.people.Notification;
import com.nonprofit.model.people.Person;

/**
 * STRATEGY PATTERN - Concrete Strategy: direct message on social media.
 */
public class SocialMediaStrategy implements NotificationStrategy {

    @Override
    public boolean send(Person to, Message msg) {
        if (to.getSocialHandle() == null || to.getSocialHandle().isBlank()) {
            return false;
        }
        to.receive(new Notification(Channel.SOCIAL, msg.getSubject() + " <" + to.getSocialHandle() + ">", msg.getBody()));
        return true;
    }

}
