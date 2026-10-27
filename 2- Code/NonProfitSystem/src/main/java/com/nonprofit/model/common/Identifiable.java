package com.nonprofit.model.common;

/**
 * Anything stored in a GenericDAO must expose a unique id.
 */
public interface Identifiable {

    int getId();
}
