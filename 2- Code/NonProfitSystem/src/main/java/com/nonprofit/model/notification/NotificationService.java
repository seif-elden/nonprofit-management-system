package com.nonprofit.model.notification;

import com.nonprofit.model.people.Person;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * STRATEGY PATTERN - Context.
 * By default the delivery strategy is picked at runtime from each
 * recipient's preferred channel ("different communication strategies for
 * different recipients"). setStrategy() forces one channel for everybody
 * (null goes back to per-recipient choice). If the chosen channel cannot
 * reach the person, e-mail is used as a fallback.
 */
public class NotificationService {

    private Map<Channel, NotificationStrategy> strategies;
    private NotificationStrategy strategy;

    public NotificationService() {
        strategies = new HashMap<>();
    }

    /** New channels are plugged in here without changing this class. */
    public void registerStrategy(Channel channel, NotificationStrategy strategy) {
        strategies.put(channel, strategy);
    }

    /** Forces one strategy for all recipients; null = use each recipient's preferred channel. */
    public void setStrategy(NotificationStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean sendNotification(Person to, Message msg) {
        NotificationStrategy chosen = strategy != null ? strategy : strategies.get(to.getPreferredChannel());
        if (chosen != null && chosen.send(to, msg)) {
            return true;
        }
        NotificationStrategy fallback = strategies.get(Channel.EMAIL);
        return fallback != null && fallback != chosen && fallback.send(to, msg);
    }

    /** Sends to everyone in the list; returns how many were reached. */
    public int broadcast(List<? extends Person> recipients, Message msg) {
        int reached = 0;
        for (Person person : recipients) {
            if (sendNotification(person, msg)) {
                reached++;
            }
        }
        return reached;
    }
}
