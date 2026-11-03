package com.nonprofit.web;

import com.nonprofit.ApplicationServices;
import com.nonprofit.model.beneficiary.*;
import com.nonprofit.model.common.Identifiable;
import com.nonprofit.model.donation.*;
import com.nonprofit.model.donation.factory.*;
import com.nonprofit.model.event.*;
import com.nonprofit.model.event.factory.*;
import com.nonprofit.model.event.ticket.Ticket;
import com.nonprofit.model.lookup.*;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.payment.*;
import com.nonprofit.model.people.*;
import com.nonprofit.model.persistence.GenericDAO;
import com.nonprofit.model.volunteer.Assignment;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Spring MVC adapter: all business operations use the existing Java model. */
@Controller
public class WebController {
    private static final Map<String, String> PAGES = Map.of(
            "dashboard", "Overview", "people", "People & accounts", "donations", "Donations",
            "events", "Events", "volunteers", "Volunteers", "beneficiaries", "Beneficiary support",
            "lookups", "Lookup tables", "activity", "Activity & inbox");
    private final ApplicationServices app = ApplicationServices.getInstance();

    @GetMapping({"/", "/dashboard", "/people", "/donations", "/events", "/volunteers",
            "/beneficiaries", "/lookups", "/activity"})
    public String page(HttpServletRequest request, @RequestParam(defaultValue = "") String q,
                       @RequestParam(required = false) Integer personId,
                       @RequestParam(required = false) Integer eventId, Model model) {
        String section = request.getServletPath().replace("/", "");
        if (section.isEmpty()) section = "dashboard";
        var db = app.db;
        model.addAttribute("section", section);
        model.addAttribute("title", PAGES.get(section));
        model.addAttribute("q", q);
        model.addAttribute("people", app.people.searchByName(q));
        model.addAttribute("allPeople", db.getAllPeople());
        model.addAttribute("donors", db.getDonors().findAll());
        model.addAttribute("volunteers", db.getVolunteers().findAll());
        model.addAttribute("beneficiaries", db.getBeneficiaries().findAll());
        model.addAttribute("campaigns", db.getCampaigns().findAll());
        model.addAttribute("donations", db.getDonations().findAll());
        model.addAttribute("events", db.getEvents().findAll());
        model.addAttribute("assignments", db.getAssignments().findAll());
        model.addAttribute("resources", db.getResources().findAll());
        model.addAttribute("distributions", db.getDistributions().findAll());
        model.addAttribute("skills", db.getSkills().findAll());
        model.addAttribute("itemCategories", db.getItemCategories().findAll());
        model.addAttribute("resourceCategories", db.getResourceCategories().findAll());
        model.addAttribute("totalRaised", app.stats.getTotalRaised());
        model.addAttribute("donationCount", app.stats.getDonationCount());
        model.addAttribute("peopleCount", db.getAllPeople().size());
        model.addAttribute("totalStock", db.getResources().findAll().stream().mapToInt(Resource::getStockQuantity).sum());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("year", LocalDate.now().getYear());
        List<String> activity = new ArrayList<>(db.getActivityLog().getEntries());
        Collections.reverse(activity);
        model.addAttribute("activity", activity);
        model.addAttribute("selectedPerson", personId == null ? null : app.people.findById(personId));
        Event selectedEvent = eventId == null ? null : db.getEvents().findById(eventId);
        model.addAttribute("selectedEvent", selectedEvent);
        model.addAttribute("suggestions", selectedEvent == null ? List.of() : app.volunteers.suggestVolunteers(selectedEvent));
        model.addAttribute("lookupGroups", List.of(
                Map.of("type", "skills", "title", "Skills", "flag", "Requires training", "items", db.getSkills().findAll()),
                Map.of("type", "items", "title", "Item categories", "flag", "Perishable", "items", db.getItemCategories().findAll()),
                Map.of("type", "resources", "title", "Resource categories", "flag", "Essential", "items", db.getResourceCategories().findAll())));
        return "app";
    }

    @PostMapping("/actions/{action}")
    public String action(@PathVariable String action, @RequestParam Map<String, String> f,
                         RedirectAttributes flash) {
        String section = switch (action) {
            case "person-create", "person-update", "person-delete" -> "people";
            case "donation", "receipt", "tax" -> "donations";
            case "event-create", "event-update", "ticket" -> "events";
            case "assignment", "hours", "certificate" -> "volunteers";
            case "distribution", "restock", "need", "impact" -> "beneficiaries";
            case "lookup-create", "lookup-update", "lookup-delete" -> "lookups";
            case "read-inbox" -> "activity";
            default -> throw new IllegalArgumentException("Unknown action.");
        };
        try {
            String message = switch (action) {
                case "person-create" -> createPerson(f);
                case "person-update" -> updatePerson(f);
                case "person-delete" -> {
                    require(app.people.delete(id(f, "personId")), "Cannot delete: this person is unknown or has linked records.");
                    yield "Person deleted.";
                }
                case "donation" -> donate(f, flash);
                case "receipt" -> {
                    Donation donation = record(app.db.getDonations(), id(f, "donationId"));
                    document(flash, "Donation receipt", app.donations.buildReceipt(donation, receiptOptions(f)).render());
                    yield "Receipt generated.";
                }
                case "tax" -> {
                    document(flash, "Annual tax statement", app.donations.generateTaxStatement(
                            record(app.db.getDonors(), id(f, "donorId")), id(f, "year")));
                    yield "Statement generated.";
                }
                case "event-create" -> createEvent(f);
                case "event-update" -> updateEvent(f);
                case "ticket" -> sellTicket(f, flash);
                case "assignment" -> {
                    Assignment assignment = app.volunteers.assign(record(app.db.getVolunteers(), id(f, "volunteerId")),
                            record(app.db.getEvents(), id(f, "eventId")), text(f, "role"));
                    require(assignment != null, "Cannot assign: check the required skill, event status, and existing assignments.");
                    yield "Volunteer assigned.";
                }
                case "hours" -> {
                    app.volunteers.logHours(record(app.db.getAssignments(), id(f, "assignmentId")), positive(f, "hours"));
                    yield "Volunteer hours updated.";
                }
                case "certificate" -> {
                    Volunteer volunteer = record(app.db.getVolunteers(), id(f, "volunteerId"));
                    require(volunteer.getTotalHours() > 0, "Record some volunteer hours before issuing a certificate.");
                    document(flash, "Volunteer certificate", app.volunteers.generateCertificate(volunteer).render());
                    yield "Certificate generated.";
                }
                case "distribution" -> {
                    Resource resource = record(app.db.getResources(), id(f, "resourceId"));
                    Distribution distribution = app.distributions.recordDistribution(
                            record(app.db.getBeneficiaries(), id(f, "beneficiaryId")), resource, id(f, "quantity"));
                    require(distribution != null, "Not enough stock. Quantity must be positive and no greater than available stock.");
                    yield "Distribution recorded. " + resource.getName() + " stock is now " + resource.getStockQuantity() + ".";
                }
                case "restock" -> {
                    Resource resource = record(app.db.getResources(), id(f, "resourceId"));
                    int quantity = id(f, "quantity");
                    require(quantity <= Integer.MAX_VALUE - resource.getStockQuantity(), "Quantity is too large.");
                    resource.addStock(quantity);
                    yield "Stock replenished.";
                }
                case "need" -> {
                    Beneficiary beneficiary = record(app.db.getBeneficiaries(), id(f, "beneficiaryId"));
                    ResourceCategory category = record(app.db.getResourceCategories(), id(f, "categoryId"));
                    int priority = id(f, "priority");
                    require(priority <= 5, "Priority must be between 1 and 5.");
                    beneficiary.addNeed(app.db.nextId(), category, text(f, "description"), priority);
                    yield "Beneficiary need added.";
                }
                case "impact" -> {
                    ResourceCategory category = record(app.db.getResourceCategories(), id(f, "categoryId"));
                    String strategy = text(f, "strategy");
                    require(Set.of("food", "education").contains(strategy), "Choose a valid impact strategy.");
                    ImpactReport report = app.distributions.evaluateImpact(category, strategy.equals("education")
                            ? new EducationImpactStrategy() : new FoodAidImpactStrategy());
                    document(flash, report.getProgramName(), report.getSummary() + "\nScore: " + report.getScore());
                    yield "Impact report generated.";
                }
                case "lookup-create", "lookup-update", "lookup-delete" -> manageLookup(action, f);
                case "read-inbox" -> {
                    person(f).getNotifications().forEach(Notification::markAsRead);
                    yield "Notifications marked as read.";
                }
                default -> throw new IllegalArgumentException("Unknown action.");
            };
            flash.addFlashAttribute("success", message);
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            flash.addFlashAttribute("error", ex instanceof NumberFormatException || ex instanceof DateTimeParseException
                    ? "Check the numbers and dates in the form." : ex.getMessage());
        }
        if (action.equals("read-inbox") && f.get("personId") != null) {
            try {
                return "redirect:/activity?personId=" + id(f, "personId");
            } catch (IllegalArgumentException ignored) { /* return to the unselected inbox */ }
        }
        return "redirect:/" + section;
    }

    private String createPerson(Map<String, String> f) {
        String type = text(f, "type");
        String name = text(f, "name");
        String email = text(f, "email");
        String password = f.getOrDefault("password", "");
        String phone = optional(f, "phone");
        String address = optional(f, "address");
        String availability = optional(f, "availability");
        int familySize = type.equals("BENEFICIARY") ? id(f, "familySize") : 1;
        Skill skill = optionalId(f, "skillId") == 0 ? null : record(app.db.getSkills(), optionalId(f, "skillId"));
        Person person = app.people.register(type, name, email, phone, password);
        require(person != null, "Use a valid role, a new email, and a password of at least 6 characters.");
        if (person instanceof Beneficiary beneficiary) beneficiary.setHousehold(address, familySize);
        if (person instanceof Volunteer volunteer) {
            if (skill != null) volunteer.addSkill(skill);
            if (!availability.isBlank()) volunteer.addAvailability(availability);
        }
        return name + " added to the community.";
    }

    private String updatePerson(Map<String, String> f) {
        Person person = person(f);
        String email = text(f, "email");
        String phone = optional(f, "phone");
        String social = optional(f, "social");
        Channel channel = Channel.valueOf(text(f, "channel"));
        require(email.contains("@") && app.people.isEmailAvailable(email, person), "Use a valid email that belongs to no other person.");
        person.updateContact(email, phone, social, channel);
        app.people.update(person);
        return "Contact details updated.";
    }

    private String donate(Map<String, String> f, RedirectAttributes flash) {
        Donor donor = record(app.db.getDonors(), id(f, "donorId"));
        Campaign campaign = record(app.db.getCampaigns(), id(f, "campaignId"));
        double amount = positive(f, "amount");
        DonationForm form;
        DonationHandler handler;
        String type = text(f, "type");
        if (type.equals("monetary")) {
            PaymentStrategy payment = switch (text(f, "payment")) {
                case "cash" -> new CashPayment(text(f, "paymentReference"));
                case "check" -> new CheckPayment(text(f, "paymentReference"), text(f, "bank"));
                // Phase 1 simulates card payments; use only a demo reference, never a real card.
                case "online" -> new OnlinePayment(donor.getName(), text(f, "paymentReference"), "Demo gateway");
                default -> throw new IllegalArgumentException("Choose a payment method.");
            };
            form = DonationForm.monetary(campaign, amount, payment);
            handler = new MonetaryDonationHandler();
        } else {
            require(type.equals("in-kind"), "Choose a donation type.");
            form = DonationForm.inKind(campaign, text(f, "item"), id(f, "quantity"), amount,
                    record(app.db.getItemCategories(), id(f, "categoryId")));
            handler = new InKindDonationHandler();
        }
        form.withReceiptOptions(receiptOptions(f));
        Donation donation = app.donations.recordDonation(handler, donor, form);
        require(donation.getStatus() == DonationStatus.COMPLETED, "Donation rejected. Check the supplied values.");
        document(flash, "Donation receipt", donation.getReceipt().render());
        return "Donation recorded. Campaign totals and the donor's inbox have been updated.";
    }

    private String createEvent(Map<String, String> f) {
        String type = text(f, "type");
        EventCreator creator = switch (type) {
            case "Fundraiser" -> new FundraiserCreator();
            case "Workshop" -> new WorkshopCreator();
            case "Outreach" -> new OutreachCreator();
            default -> throw new IllegalArgumentException("Choose an event type.");
        };
        int skillId = optionalId(f, "skillId");
        EventForm form = new EventForm(text(f, "name"), LocalDate.parse(text(f, "date")), text(f, "venue"),
                id(f, "capacity"), nonnegative(f, "price"), skillId == 0 ? null : record(app.db.getSkills(), skillId));
        switch (type) {
            case "Fundraiser" -> form.setFundraiserDetails(positive(f, "target"));
            case "Workshop" -> form.setWorkshopDetails(text(f, "trainer"), text(f, "topic"));
            case "Outreach" -> form.setOutreachDetails(text(f, "location"), id(f, "teamSize"));
        }
        require(app.events.createEvent(creator, form) != null, "Event not created. Use a current/future date, positive capacity, and nonnegative price.");
        return "Event created.";
    }

    private String updateEvent(Map<String, String> f) {
        Event event = record(app.db.getEvents(), id(f, "eventId"));
        require(event.isOpen(), "This event is already closed.");
        switch (text(f, "operation")) {
            case "register" -> require(app.events.registerAttendee(event, person(f)), "Already registered, or the event is full.");
            case "attendance" -> require(app.events.markAttendance(event, person(f)), "Register this person first; attendance can be marked once.");
            case "date" -> require(app.events.changeDate(event, LocalDate.parse(text(f, "date"))), "The new date cannot be in the past.");
            case "venue" -> app.events.changeVenue(event, text(f, "venue"));
            case "remind" -> app.events.sendReminders(event);
            case "cancel" -> app.events.cancelEvent(event);
            case "complete" -> app.events.completeEvent(event, nonnegative(f, "hours"));
            default -> throw new IllegalArgumentException("Choose a valid event action.");
        }
        return "Event updated.";
    }

    private String sellTicket(Map<String, String> f, RedirectAttributes flash) {
        Event event = record(app.db.getEvents(), id(f, "eventId"));
        require(event.isOpen(), "This event is already closed.");
        List<String> extras = new ArrayList<>();
        if (f.containsKey("vip")) extras.add("VIP");
        if (f.containsKey("meal")) extras.add("MEAL");
        Ticket ticket = app.events.sellTicket(event, person(f), extras);
        require(ticket != null, "The event is full or closed.");
        document(flash, "Event ticket", ticket.getDescription() + String.format("\nTotal: %.2f EGP", ticket.getPrice()));
        return "Ticket issued and attendee registered.";
    }

    private String manageLookup(String action, Map<String, String> f) {
        String type = text(f, "type");
        GenericDAO<? extends LookupItem> table = switch (type) {
            case "skills" -> app.db.getSkills();
            case "items" -> app.db.getItemCategories();
            case "resources" -> app.db.getResourceCategories();
            default -> throw new IllegalArgumentException("Unknown lookup table.");
        };
        if (action.equals("lookup-delete")) {
            LookupItem item = record(table, id(f, "id"));
            require(!lookupInUse(item), "This lookup is referenced by another record and cannot be deleted.");
            table.delete(item.getId());
            return "Lookup deleted.";
        }
        String name = text(f, "name");
        String description = optional(f, "description");
        if (action.equals("lookup-update")) {
            LookupItem item = record(table, id(f, "id"));
            item.setName(name);
            item.setDescription(description);
            return "Lookup updated.";
        }
        boolean flag = f.containsKey("flag");
        switch (type) {
            case "skills" -> app.db.getSkills().insert(new Skill(app.db.nextId(), name, description, flag));
            case "items" -> app.db.getItemCategories().insert(new ItemCategory(app.db.nextId(), name, description, flag));
            case "resources" -> app.db.getResourceCategories().insert(new ResourceCategory(app.db.nextId(), name, description, flag));
        }
        return "Lookup added.";
    }

    private boolean lookupInUse(LookupItem item) {
        return app.db.getEvents().findAll().stream().anyMatch(e -> e.getRequiredSkill() == item)
                || app.db.getVolunteers().findAll().stream().anyMatch(v -> v.getSkills().contains(item))
                || app.db.getDonations().findAll().stream().anyMatch(d -> d instanceof InKindDonation k && k.getCategory() == item)
                || app.db.getResources().findAll().stream().anyMatch(r -> r.getCategory() == item)
                || app.db.getBeneficiaries().findAll().stream().flatMap(b -> b.getNeeds().stream()).anyMatch(n -> n.getCategory() == item);
    }

    private Person person(Map<String, String> f) {
        Person person = app.people.findById(id(f, "personId"));
        require(person != null, "Person not found.");
        return person;
    }

    private static <T extends Identifiable> T record(GenericDAO<T> table, int id) {
        T item = table.findById(id);
        require(item != null, "Record not found in " + table.getTableName() + ".");
        return item;
    }

    private static String optional(Map<String, String> f, String key) {
        String value = f.getOrDefault(key, "").trim();
        require(value.length() <= 1000, "The " + key + " field is too long.");
        return value;
    }

    private static String text(Map<String, String> f, String key) {
        String value = optional(f, key);
        require(!value.isBlank(), "The " + key + " field is required.");
        return value;
    }

    private static int id(Map<String, String> f, String key) {
        int value = Integer.parseInt(text(f, key));
        require(value > 0, "The " + key + " field must be positive.");
        return value;
    }

    private static int optionalId(Map<String, String> f, String key) {
        String value = optional(f, key);
        if (value.isEmpty() || value.equals("0")) return 0;
        return id(f, key);
    }

    private static double nonnegative(Map<String, String> f, String key) {
        double value = Double.parseDouble(text(f, key));
        require(Double.isFinite(value) && value >= 0, "The " + key + " field must be a finite, nonnegative number.");
        return value;
    }

    private static double positive(Map<String, String> f, String key) {
        double value = nonnegative(f, key);
        require(value > 0, "The " + key + " field must be greater than zero.");
        return value;
    }

    private static List<String> receiptOptions(Map<String, String> f) {
        List<String> options = new ArrayList<>();
        if (f.containsKey("tax")) options.add("TAX");
        if (f.containsKey("thanks")) options.add("THANK_YOU");
        return options;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

    private static void document(RedirectAttributes flash, String title, String content) {
        flash.addFlashAttribute("documentTitle", title);
        flash.addFlashAttribute("document", content);
    }
}
