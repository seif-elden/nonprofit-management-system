package com.nonprofit.view;

import com.nonprofit.model.people.Notification;
import com.nonprofit.model.people.Person;
import java.util.List;

/**
 * MVC - View for the main menu, notification inboxes and the activity log.
 */
public class MainView extends View {

    @Override
    public int show() {
        showTitle("Hope Bridge - Non-Profit Management System");
        showMessage("1. People and accounts");
        showMessage("2. Donations");
        showMessage("3. Events");
        showMessage("4. Volunteers");
        showMessage("5. Beneficiaries");
        showMessage("6. Lookup tables (admin)");
        showMessage("7. View a person's notifications");
        showMessage("8. View activity log");
        showMessage("0. Exit");
        return readInt("Choose: ");
    }

    public void showNotifications(Person person) {
        showTitle("Notifications for " + person.getName());
        List<Notification> notifications = person.getNotifications();
        if (notifications.isEmpty()) {
            showMessage("(no notifications)");
        }
        for (Notification n : notifications) {
            showMessage("  " + (n.isRead() ? "   " : "NEW") + " [" + n.getChannel() + "] " + n.getSubject());
            showMessage("        " + n.getBody());
        }
    }

    public void showActivityLog(List<String> entries) {
        showTitle("Activity Log");
        showLines(entries);
    }
}
