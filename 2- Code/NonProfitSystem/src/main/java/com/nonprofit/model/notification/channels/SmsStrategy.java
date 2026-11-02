package com.nonprofit.model.notification.channels;

import com.nonprofit.model.notification.Message;
import com.nonprofit.model.notification.NotificationStrategy;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.people.Notification;
import com.nonprofit.model.people.Person;

/**
 * STRATEGY PATTERN - Concrete Strategy: SMS. Texts are short, so only
 * the first 160 characters of the body are sent.
 */
public class SmsStrategy implements NotificationStrategy {

    private static final int SMS_LIMIT = 160;

    @Override
    public boolean send(Person to, Message msg) {
        if (to.getPhone() == null || to.getPhone().isBlank()) {
            return false;
        }
        String text = msg.getBody().length() > SMS_LIMIT ? msg.getBody().substring(0, SMS_LIMIT) : msg.getBody();
        to.receive(new Notification(Channel.SMS, msg.getSubject() + " <" + to.getPhone() + ">", text));
        return true;
    }

}
