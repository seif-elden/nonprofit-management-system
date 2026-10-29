package com.nonprofit.model.observer;

/**
 * OBSERVER PATTERN - Subject ("Publisher").
 * Implemented by DonationService, EventService and DistributionService.
 */
public interface Subject {

    void addObserver(Observer o);

    void removeObserver(Observer o);

    void notifyObservers(ChangeEvent change);
}
