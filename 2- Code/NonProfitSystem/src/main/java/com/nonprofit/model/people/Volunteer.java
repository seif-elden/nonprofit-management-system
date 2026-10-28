package com.nonprofit.model.people;

import com.nonprofit.model.lookup.Skill;
import java.util.ArrayList;
import java.util.List;

/**
 * A person who gives time. Skills come from the Skill lookup table;
 * preferences name the event types the volunteer likes (e.g. "Workshop").
 */
public class Volunteer extends Person {

    private List<Skill> skills;
    private List<String> availability;
    private List<String> preferences;
    private double totalHours;

    public Volunteer(int id, String name, String email, String phone, String passwordHash) {
        super(id, name, email, phone, passwordHash);
        this.skills = new ArrayList<>();
        this.availability = new ArrayList<>();
        this.preferences = new ArrayList<>();
        this.totalHours = 0;
    }

    public void addSkill(Skill skill) {
        if (!skills.contains(skill)) {
            skills.add(skill);
        }
    }

    public boolean hasSkill(Skill skill) {
        return skills.contains(skill);
    }

    public void addAvailability(String slot) {
        availability.add(slot);
    }

    public void addPreference(String eventType) {
        preferences.add(eventType);
    }

    public boolean prefers(String eventType) {
        for (String preference : preferences) {
            if (preference.equalsIgnoreCase(eventType)) {
                return true;
            }
        }
        return false;
    }

    public void addHours(double hours) {
        totalHours += hours;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<String> getAvailability() {
        return availability;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public double getTotalHours() {
        return totalHours;
    }

    @Override
    public String getRole() {
        return "Volunteer";
    }
}
