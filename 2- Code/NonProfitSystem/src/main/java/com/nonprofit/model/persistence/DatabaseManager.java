package com.nonprofit.model.persistence;

import com.nonprofit.model.beneficiary.Distribution;
import com.nonprofit.model.beneficiary.Resource;
import com.nonprofit.model.donation.Campaign;
import com.nonprofit.model.donation.Donation;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.event.Fundraiser;
import com.nonprofit.model.event.Outreach;
import com.nonprofit.model.event.Workshop;
import com.nonprofit.model.lookup.ItemCategory;
import com.nonprofit.model.lookup.ResourceCategory;
import com.nonprofit.model.lookup.Skill;
import com.nonprofit.model.observer.ActivityLog;
import com.nonprofit.model.people.Beneficiary;
import com.nonprofit.model.notification.Channel;
import com.nonprofit.model.people.Donor;
import com.nonprofit.model.people.Person;
import com.nonprofit.model.people.PersonService;
import com.nonprofit.model.people.Volunteer;
import com.nonprofit.model.volunteer.Assignment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SINGLETON PATTERN.
 * The single data store: one GenericDAO per kind of record. Every service
 * and controller must see the same data, so only one instance may exist
 * (two copies would hold different data, like two chocolate boilers).
 * Phase 1 keeps the data in memory; Phase 2 can swap in a real database
 * behind this same class.
 */
public class DatabaseManager {

    private static DatabaseManager instance;

    private String organizationName;
    private String taxRegistrationNo;
    private int lastId;
    private GenericDAO<Donor> donors;
    private GenericDAO<Volunteer> volunteers;
    private GenericDAO<Beneficiary> beneficiaries;
    private GenericDAO<Campaign> campaigns;
    private GenericDAO<Donation> donations;
    private GenericDAO<Event> events;
    private GenericDAO<Assignment> assignments;
    private GenericDAO<Resource> resources;
    private GenericDAO<Distribution> distributions;
    private GenericDAO<Skill> skills;
    private GenericDAO<ItemCategory> itemCategories;
    private GenericDAO<ResourceCategory> resourceCategories;
    private ActivityLog activityLog;

    /** Private constructor: no other class can call "new DatabaseManager()". */
    private DatabaseManager() {
        organizationName = "Hope Bridge Foundation";
        taxRegistrationNo = "NPO-2026-0451";
        lastId = 0;
        donors = new GenericDAO<>("Donors");
        volunteers = new GenericDAO<>("Volunteers");
        beneficiaries = new GenericDAO<>("Beneficiaries");
        campaigns = new GenericDAO<>("Campaigns");
        donations = new GenericDAO<>("Donations");
        events = new GenericDAO<>("Events");
        assignments = new GenericDAO<>("Assignments");
        resources = new GenericDAO<>("Resources");
        distributions = new GenericDAO<>("Distributions");
        skills = new GenericDAO<>("Skills");
        itemCategories = new GenericDAO<>("Item Categories");
        resourceCategories = new GenericDAO<>("Resource Categories");
        activityLog = new ActivityLog();
        loadSampleData();
    }

    /** Global access point; synchronized so two threads cannot create two instances. */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public int nextId() {
        lastId++;
        return lastId;
    }

    /** Everyone registered in the system. */
    public List<Person> getAllPeople() {
        List<Person> people = new ArrayList<>();
        people.addAll(donors.findAll());
        people.addAll(volunteers.findAll());
        people.addAll(beneficiaries.findAll());
        return people;
    }

    /** Starting data so every feature can be demonstrated straight away (password: pass123). */
    private void loadSampleData() {
        String pw = PersonService.hashPassword("pass123");

        Skill firstAid = new Skill(nextId(), "First Aid", "Basic medical help", true);
        Skill teaching = new Skill(nextId(), "Teaching", "Running classes and workshops", false);
        Skill driving = new Skill(nextId(), "Driving", "Transporting goods and people", true);
        skills.insert(firstAid);
        skills.insert(teaching);
        skills.insert(driving);

        itemCategories.insert(new ItemCategory(nextId(), "Food", "Dry and fresh food", true));
        itemCategories.insert(new ItemCategory(nextId(), "Clothes", "New or gently used clothes", false));
        itemCategories.insert(new ItemCategory(nextId(), "Medicine", "Sealed medicine and supplies", true));

        ResourceCategory foodAid = new ResourceCategory(nextId(), "Food Aid", "Food boxes and meals", true);
        ResourceCategory education = new ResourceCategory(nextId(), "Education", "School kits and tutoring", false);
        ResourceCategory medical = new ResourceCategory(nextId(), "Medical", "First-aid and medical supplies", true);
        resourceCategories.insert(foodAid);
        resourceCategories.insert(education);
        resourceCategories.insert(medical);

        Donor omar = new Donor(nextId(), "Omar Hassan", "omar@mail.com", "01001234567", pw, "Individual", "TAX-1001");
        Donor mona = new Donor(nextId(), "Mona Adel", "mona@nilecorp.com", "01112345678", pw, "Corporate", "TAX-2002");
        mona.updateContact("mona@nilecorp.com", "01112345678", "", Channel.SMS);
        donors.insert(omar);
        donors.insert(mona);

        Volunteer laila = new Volunteer(nextId(), "Laila Fathy", "laila@mail.com", "01223456789", pw);
        laila.updateContact("laila@mail.com", "01223456789", "@laila.helps", Channel.SOCIAL);
        laila.addSkill(teaching);
        laila.addSkill(firstAid);
        laila.addAvailability("Weekends");
        laila.addPreference("Workshop");
        Volunteer karim = new Volunteer(nextId(), "Karim Nabil", "karim@mail.com", "01534567890", pw);
        karim.updateContact("karim@mail.com", "01534567890", "", Channel.SMS);
        karim.addSkill(driving);
        karim.addAvailability("Weekdays");
        karim.addPreference("Outreach");
        volunteers.insert(laila);
        volunteers.insert(karim);

        Beneficiary amal = new Beneficiary(nextId(), "Amal Saeed", "amal@mail.com", "01045678901", pw, "Helwan, Cairo", 5);
        amal.addNeed(nextId(), foodAid, "Monthly food support", 1);
        amal.addNeed(nextId(), education, "School supplies for 3 children", 2);
        Beneficiary hany = new Beneficiary(nextId(), "Hany Mostafa", "hany@mail.com", "01156789012", pw, "Imbaba, Giza", 3);
        hany.addNeed(nextId(), medical, "Diabetes supplies", 1);
        beneficiaries.insert(amal);
        beneficiaries.insert(hany);

        campaigns.insert(new Campaign(nextId(), "Winter Blankets 2026", 50000));

        events.insert(new Fundraiser(nextId(), "Winter Charity Gala", LocalDate.now().plusDays(14),
                "Cairo Marriott", 100, 500, null, 200000));
        events.insert(new Workshop(nextId(), "Literacy Workshop", LocalDate.now().plusDays(7),
                "Community Center, Giza", 30, 0, teaching, "Dr. Nadia Samir", "Adult literacy"));
        events.insert(new Outreach(nextId(), "Food Convoy", LocalDate.now().plusDays(10),
                "Helwan", 20, 0, driving, "Helwan", 6));

        resources.insert(new Resource(nextId(), "Food box", "box", 120, foodAid));
        resources.insert(new Resource(nextId(), "School kit", "kit", 60, education));
        resources.insert(new Resource(nextId(), "First-aid kit", "kit", 40, medical));
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getTaxRegistrationNo() {
        return taxRegistrationNo;
    }

    public GenericDAO<Donor> getDonors() {
        return donors;
    }

    public GenericDAO<Volunteer> getVolunteers() {
        return volunteers;
    }

    public GenericDAO<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    public GenericDAO<Campaign> getCampaigns() {
        return campaigns;
    }

    public GenericDAO<Donation> getDonations() {
        return donations;
    }

    public GenericDAO<Event> getEvents() {
        return events;
    }

    public GenericDAO<Assignment> getAssignments() {
        return assignments;
    }

    public GenericDAO<Resource> getResources() {
        return resources;
    }

    public GenericDAO<Distribution> getDistributions() {
        return distributions;
    }

    public GenericDAO<Skill> getSkills() {
        return skills;
    }

    public GenericDAO<ItemCategory> getItemCategories() {
        return itemCategories;
    }

    public GenericDAO<ResourceCategory> getResourceCategories() {
        return resourceCategories;
    }

    public ActivityLog getActivityLog() {
        return activityLog;
    }
}
