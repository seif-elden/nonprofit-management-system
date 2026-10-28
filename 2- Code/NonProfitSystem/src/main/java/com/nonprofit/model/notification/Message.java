package com.nonprofit.model.notification;

import java.time.LocalDateTime;

/**
 * Channel-independent content of a notification.
 */
public class Message {

    private String subject;
    private String body;
    private LocalDateTime createdAt;

    public Message(String subject, String body) {
        this.subject = subject;
        this.body = body;
        this.createdAt = LocalDateTime.now();
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
