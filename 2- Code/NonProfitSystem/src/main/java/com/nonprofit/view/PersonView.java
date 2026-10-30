package com.nonprofit.view;

import com.nonprofit.model.lookup.Skill;
import com.nonprofit.model.people.Beneficiary;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.people.Donor;
import com.nonprofit.model.people.Need;
import com.nonprofit.model.people.Person;
import com.nonprofit.model.people.Volunteer;

/**
 * MVC - View for sign-up, log-in and people management.
 */
public class PersonView extends View {

    @Override
    public int show() {
        showTitle("People and Accounts");
        showMessage("1. Sign up");
        showMessage("2. Log in");
        showMessage("3. Search people by name");
        showMessage("4. Update contact details");
        showMessage("5. Delete a person");
        showMessage("0. Back");
        return readInt("Choose: ");
    }

    public void showSignupForm() {
        showTitle("Sign up");
    }

    public void showLoginForm() {
        showTitle("Log in");
    }

    public int askRole() {
        showMessage("Sign up as: 1. Donor   2. Volunteer   3. Beneficiary");
        return readInt("Choose: ");
    }

    public Channel askChannel() {
        showMessage("Preferred channel: 1. Email   2. SMS   3. Social media");
        int choice = readInt("Choose: ");
        return choice == 2 ? Channel.SMS : choice == 3 ? Channel.SOCIAL : Channel.EMAIL;
    }

    public void showProfile(Person person) {
        showTitle("Profile: " + person.getName());
        showMessage("  Id / role      : " + person.getId() + " / " + person.getRole());
        showMessage("  E-mail / phone : " + person.getEmail() + " / " + person.getPhone());
        showMessage("  Social handle  : " + (person.getSocialHandle().isBlank() ? "-" : person.getSocialHandle()));
        showMessage("  Preferred      : " + person.getPreferredChannel());
        if (person instanceof Donor donor) {
            showMessage("  Donor type     : " + donor.getDonorType() + ", tax id " + donor.getTaxId());
            showMessage(String.format("  Total donated  : %.2f EGP", donor.getTotalDonated()));
        } else if (person instanceof Volunteer volunteer) {
            StringBuilder skills = new StringBuilder();
            for (Skill skill : volunteer.getSkills()) {
                skills.append(skill.getName()).append(" ");
            }
            showMessage("  Skills         : " + skills);
            showMessage("  Availability   : " + volunteer.getAvailability() + ", prefers " + volunteer.getPreferences());
            showMessage("  Total hours    : " + volunteer.getTotalHours());
        } else if (person instanceof Beneficiary beneficiary) {
            showMessage("  Address        : " + beneficiary.getAddress() + ", family of " + beneficiary.getFamilySize());
            for (Need need : beneficiary.getNeeds()) {
                showMessage("  Need (p" + need.getPriority() + ")      : " + need.getCategory().getName() + " - " + need.getDescription());
            }
        }
        long unread = person.getNotifications().stream().filter(n -> !n.isRead()).count();
        showMessage("  Notifications  : " + unread + " unread");
    }
}
