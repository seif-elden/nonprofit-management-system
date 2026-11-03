package com.nonprofit.web;

import com.nonprofit.ApplicationServices;
import com.nonprofit.model.beneficiary.Resource;
import com.nonprofit.model.event.Event;
import com.nonprofit.model.people.Beneficiary;
import com.nonprofit.model.people.Donor;
import com.nonprofit.model.volunteer.Assignment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebIntegrationTest {
    @Autowired MockMvc mvc;
    private final ApplicationServices app = ApplicationServices.getInstance();

    private MockHttpSession session() throws Exception {
        return (MockHttpSession) mvc.perform(get("/")).andExpect(status().isOk())
                .andReturn().getRequest().getSession(false);
    }

    private ResultActions submit(MockHttpSession session, String action, Map<String, String> fields) throws Exception {
        var request = post("/actions/" + action).session(session)
                .param("_csrf", (String) session.getAttribute("csrf"));
        fields.forEach((key, value) -> request.param(key, value));
        return mvc.perform(request);
    }

    @Test
    void everyPageRendersItsOwnContentAndAssetsAreServed() throws Exception {
        for (var page : Map.of("dashboard", "Building bridges.", "people", "Community directory",
                "donations", "Record a donation", "events", "Create an event", "volunteers", "Find the right match",
                "beneficiaries", "Record a distribution", "lookups", "Add a record", "activity", "Community inbox").entrySet()) {
            mvc.perform(get("/" + page.getKey()).servletPath("/" + page.getKey()))
                    .andExpect(status().isOk()).andExpect(content().string(containsString(page.getValue())))
                    .andExpect(content().string(containsString("Hope Bridge")));
        }
        mvc.perform(get("/css/app.css")).andExpect(status().isOk());
        mvc.perform(get("/app.js")).andExpect(status().isOk());
    }

    @Test
    void formsRejectMissingAndForeignSessionTokens() throws Exception {
        var first = session();
        var second = session();
        mvc.perform(post("/actions/restock").session(first))
                .andExpect(status().isForbidden());
        mvc.perform(post("/actions/restock").session(second)
                .param("_csrf", (String) first.getAttribute("csrf")))
                .andExpect(status().isForbidden());
    }

    @Test
    void distributionUpdatesStockAndHistoryExactlyOnceAndRejectsInvalidQuantities() throws Exception {
        var session = session();
        Resource resource = app.db.getResources().findAll().get(0);
        Beneficiary beneficiary = app.db.getBeneficiaries().findAll().get(0);
        int stock = resource.getStockQuantity();
        int history = app.db.getDistributions().findAll().size();
        var fields = Map.of("resourceId", "" + resource.getId(), "beneficiaryId", "" + beneficiary.getId(), "quantity", "3");
        submit(session, "distribution", fields).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/beneficiaries")).andExpect(flash().attributeExists("success"));
        assertThat(resource.getStockQuantity()).isEqualTo(stock - 3);
        assertThat(app.db.getDistributions().findAll()).hasSize(history + 1);
        for (String quantity : new String[]{"0", "-1", "2147483647", "abc"}) {
            submit(session, "distribution", Map.of("resourceId", "" + resource.getId(),
                    "beneficiaryId", "" + beneficiary.getId(), "quantity", quantity))
                    .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("error"));
        }
        assertThat(resource.getStockQuantity()).isEqualTo(stock - 3);
        assertThat(app.db.getDistributions().findAll()).hasSize(history + 1);
    }

    @Test
    void donationTriggersReceiptTotalsAndDonorNotification() throws Exception {
        var session = session();
        Donor donor = app.db.getDonors().findAll().get(0);
        var campaign = app.db.getCampaigns().findAll().get(0);
        double total = app.stats.getTotalRaised();
        double raised = campaign.getRaisedAmount();
        int notifications = donor.getNotifications().size();
        int count = app.stats.getDonationCount();
        submit(session, "donation", Map.of("donorId", "" + donor.getId(), "campaignId", "" + campaign.getId(),
                "type", "monetary", "payment", "cash", "paymentReference", "Test reception", "amount", "250.50", "tax", "on", "thanks", "on"))
                .andExpect(redirectedUrl("/donations")).andExpect(flash().attributeExists("document"));
        assertThat(app.stats.getTotalRaised()).isEqualTo(total + 250.50);
        assertThat(campaign.getRaisedAmount()).isEqualTo(raised + 250.50);
        assertThat(app.stats.getDonationCount()).isEqualTo(count + 1);
        assertThat(donor.getNotifications()).hasSize(notifications + 1);
        assertThat(app.db.getDonations().findAll().get(app.db.getDonations().findAll().size() - 1)
                .getReceipt().render()).contains(donor.getName(), "250.50");
        submit(session, "donation", Map.of("donorId", "" + donor.getId(), "campaignId", "" + campaign.getId(),
                "type", "monetary", "amount", "NaN")).andExpect(flash().attributeExists("error"));
        assertThat(app.stats.getTotalRaised()).isEqualTo(total + 250.50);
    }

    @Test
    void eventVolunteerTicketAndCertificateWorkflowUsesExistingServices() throws Exception {
        var session = session();
        submit(session, "event-create", Map.of("type", "Fundraiser", "name", "Integration test fundraiser",
                "date", java.time.LocalDate.now().plusDays(30).toString(), "venue", "Community hall",
                "capacity", "10", "price", "100", "target", "5000"))
                .andExpect(flash().attributeExists("success"));
        Event event = app.db.getEvents().findAll().stream().filter(e -> e.getName().equals("Integration test fundraiser")).findFirst().orElseThrow();
        var volunteer = app.db.getVolunteers().findAll().get(0);
        submit(session, "assignment", Map.of("volunteerId", "" + volunteer.getId(), "eventId", "" + event.getId(), "role", "Coordinator"))
                .andExpect(flash().attributeExists("success"));
        Assignment assignment = event.getAssignments().get(0);
        double hours = volunteer.getTotalHours();
        submit(session, "hours", Map.of("assignmentId", "" + assignment.getId(), "hours", "2.5"))
                .andExpect(flash().attributeExists("success"));
        assertThat(volunteer.getTotalHours()).isEqualTo(hours + 2.5);
        submit(session, "certificate", Map.of("volunteerId", "" + volunteer.getId()))
                .andExpect(flash().attribute("document", containsString(volunteer.getName())));
        Donor donor = app.db.getDonors().findAll().get(0);
        submit(session, "ticket", Map.of("eventId", "" + event.getId(), "personId", "" + donor.getId(), "vip", "on", "meal", "on"))
                .andExpect(flash().attribute("document", containsString("500.00 EGP")));
        assertThat(event.getAttendees()).contains(donor);
        int inbox = donor.getNotifications().size();
        submit(session, "event-update", Map.of("eventId", "" + event.getId(), "operation", "venue", "venue", "New hall"))
                .andExpect(flash().attributeExists("success"));
        assertThat(donor.getNotifications()).hasSize(inbox + 1);
        submit(session, "event-update", Map.of("eventId", "" + event.getId(), "operation", "cancel"))
                .andExpect(flash().attributeExists("success"));
        submit(session, "ticket", Map.of("eventId", "" + event.getId(), "personId", "" + donor.getId()))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void lookupCrudProtectsReferencedRecords() throws Exception {
        var session = session();
        var used = app.db.getResourceCategories().findAll().get(0);
        submit(session, "lookup-delete", Map.of("type", "resources", "id", "" + used.getId()))
                .andExpect(flash().attributeExists("error"));
        assertThat(app.db.getResourceCategories().findById(used.getId())).isSameAs(used);
        submit(session, "lookup-create", Map.of("type", "skills", "name", "Test skill", "description", "Initial"))
                .andExpect(flash().attributeExists("success"));
        var skill = app.db.getSkills().findAll().stream().filter(s -> s.getName().equals("Test skill")).findFirst().orElseThrow();
        submit(session, "lookup-update", Map.of("type", "skills", "id", "" + skill.getId(), "name", "Updated skill", "description", "Edited"))
                .andExpect(flash().attributeExists("success"));
        assertThat(skill.getName()).isEqualTo("Updated skill");
        submit(session, "lookup-delete", Map.of("type", "skills", "id", "" + skill.getId()))
                .andExpect(flash().attributeExists("success"));
        assertThat(app.db.getSkills().findById(skill.getId())).isNull();
    }

    @Test
    void peopleRegistrationSearchAndInvalidInput() throws Exception {
        var session = session();
        int before = app.db.getAllPeople().size();
        submit(session, "person-create", Map.of("type", "BENEFICIARY", "name", "Test household", "email", "household@example.test",
                "password", "pass123", "familySize", "4", "address", "Cairo"))
                .andExpect(flash().attributeExists("success"));
        assertThat(app.db.getAllPeople()).hasSize(before + 1);
        mvc.perform(get("/people").servletPath("/people").param("q", "Test household"))
                .andExpect(content().string(containsString("household@example.test")));
        submit(session, "person-create", Map.of("type", "DONOR", "name", "Duplicate", "email", "household@example.test", "password", "pass123"))
                .andExpect(flash().attributeExists("error"));
        assertThat(app.db.getAllPeople()).hasSize(before + 1);
        submit(session, "restock", Map.of("resourceId", "999999", "quantity", "3"))
                .andExpect(flash().attributeExists("error"));
    }
}
