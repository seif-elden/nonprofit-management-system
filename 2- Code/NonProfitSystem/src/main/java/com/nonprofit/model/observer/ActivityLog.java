package com.nonprofit.model.observer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER PATTERN - Concrete Observer: audit trail of every change
 * published by the subjects it watches.
 */
public class ActivityLog implements Observer {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private List<String> entries;

    public ActivityLog() {
        entries = new ArrayList<>();
    }

    /** The log subscribes itself: subject.addObserver(this). */
    public void watch(Subject subject) {
        subject.addObserver(this);
    }

    @Override
    public void update(Subject source, ChangeEvent change) {
        entries.add("[" + change.getTimestamp().format(FORMAT) + "] " + change.getType() + ": " + change.getDescription());
    }

    public List<String> getEntries() {
        return entries;
    }
}
