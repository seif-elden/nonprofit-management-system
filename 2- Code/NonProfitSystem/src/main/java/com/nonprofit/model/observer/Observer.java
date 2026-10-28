package com.nonprofit.model.observer;

/**
 * OBSERVER PATTERN - Observer ("Subscriber").
 * Receives the subject that changed and what changed.
 */
public interface Observer {

    void update(Subject source, ChangeEvent change);
}
