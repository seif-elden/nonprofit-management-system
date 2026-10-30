package com.nonprofit.view;

import com.nonprofit.model.document.Document;
import com.nonprofit.model.lookup.LookupItem;
import com.nonprofit.model.people.Person;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * MVC - View base class (abstract: every view shares the same console
 * input and output helpers).
 * All console input and output lives in the view layer; model and
 * controller classes never print anything themselves.
 */
public abstract class View {

    protected static final Scanner SCANNER = new Scanner(System.in);

    /** Shows this view's menu and returns the option the user picked. */
    public abstract int show();

    public void showTitle(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String message) {
        System.out.println("[!] " + message);
    }

    public void showLines(List<String> lines) {
        if (lines.isEmpty()) {
            showMessage("(nothing to show)");
        }
        for (String line : lines) {
            showMessage("  " + line);
        }
    }

    public String readText(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            System.out.println();
            System.out.println("Input closed. Goodbye.");
            System.exit(0);
        }
        return SCANNER.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readText(prompt));
            } catch (NumberFormatException e) {
                showError("Please enter a whole number.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readText(prompt));
            } catch (NumberFormatException e) {
                showError("Please enter a number.");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        return readText(prompt + " (y/n): ").equalsIgnoreCase("y");
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(readText(prompt + " (yyyy-mm-dd): "));
            } catch (DateTimeParseException e) {
                showError("Please use the format yyyy-mm-dd.");
            }
        }
    }

    public void showLookupItems(List<? extends LookupItem> items) {
        if (items.isEmpty()) {
            showMessage("(no rows)");
        }
        for (LookupItem item : items) {
            System.out.printf("  %-4d %-16s %s%n", item.getId(), item.getName(), item.getDescription());
        }
    }

    public void showPeople(List<? extends Person> people) {
        if (people.isEmpty()) {
            showMessage("(nobody found)");
        }
        for (Person person : people) {
            System.out.printf("  %-4d %-20s %-12s %-22s prefers %s%n", person.getId(), person.getName(),
                    person.getRole(), person.getEmail(), person.getPreferredChannel());
        }
    }

    public void showDocument(Document document) {
        System.out.println();
        System.out.println("---------------- " + document.getTitle() + " ----------------");
        System.out.println(document.render());
        System.out.println("-----------------------------------------------------------");
    }
}
