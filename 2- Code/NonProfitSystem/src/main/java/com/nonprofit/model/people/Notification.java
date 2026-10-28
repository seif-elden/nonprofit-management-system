package com.nonprofit.model.people;

import com.nonprofit.model.notification.Channel;
import java.time.LocalDateTime;

/**
 * A message delivered to a person through one channel. Created only when
 * a NotificationStrategy delivers a message, so it belongs to its owner
 * (composition).
 */
public class Notification {

    private Channel channel;
    private String subject;
    private String body;
    private LocalDateTime receivedAt;
    private boolean read;

    public Notification(Channel channel, String subject, String body) {
        this.channel = channel;
        this.subject = subject;
        this.body = body;
        this.receivedAt = LocalDateTime.now();
        this.read = false;
    }

    public void markAsRead() {
        read = true;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public boolean isRead() {
        return read;
    }
}
