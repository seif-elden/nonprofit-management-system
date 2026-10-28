package com.nonprofit.model.people;

import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.common.Identifiable;
import java.util.ArrayList;
import java.util.List;

/**
 * Common base for everyone the organization works with.
 */
public abstract class Person implements Identifiable {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String socialHandle;
    private Channel preferredChannel;
    private String passwordHash;
    private List<Notification> notifications;

    public Person(int id, String name, String email, String phone, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.socialHandle = "";
        this.preferredChannel = Channel.EMAIL;
        this.notifications = new ArrayList<>();
    }

    /** Each kind of person describes its own role. */
    public abstract String getRole();

    /** Called by a NotificationStrategy when a message is delivered. */
    public void receive(Notification notification) {
        notifications.add(notification);
    }

    public boolean checkPassword(String hash) {
        return passwordHash.equals(hash);
    }

    public void updateContact(String email, String phone, String socialHandle, Channel preferredChannel) {
        this.email = email;
        this.phone = phone;
        this.socialHandle = socialHandle;
        this.preferredChannel = preferredChannel;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSocialHandle() {
        return socialHandle;
    }

    public Channel getPreferredChannel() {
        return preferredChannel;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
