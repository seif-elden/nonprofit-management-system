package com.nonprofit.model.notification.channels;

import com.nonprofit.model.notification.Message;
import com.nonprofit.model.notification.NotificationStrategy;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.people.Notification;
import com.nonprofit.model.people.Person;

/**
 * STRATEGY PATTERN - Concrete Strategy: e-mail (simulated in Phase 1:
 * the message is placed in the person's inbox instead of a real mail server).
 */
public class EmailStrategy implements NotificationStrategy {

    @Override
    public boolean send(Person to, Message msg) {
        if (to.getEmail() == null || to.getEmail().isBlank()) {
            return false;
        }
        to.receive(new Notification(Channel.EMAIL, msg.getSubject() + " <" + to.getEmail() + ">", msg.getBody()));
        return true;
    }

}
