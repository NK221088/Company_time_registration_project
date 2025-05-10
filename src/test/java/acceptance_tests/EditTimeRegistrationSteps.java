package acceptance_tests;

import dtu.timemanager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EditTimeRegistrationSteps {
    private TimeManager timeManager;
    private ProjectHolder projectHolder;
    private Project project;
    private ErrorMessageHolder errorMessage;
    private Activity activity;
    private ActivityHolder activityHolder;

    private TimeRegistration timeRegistration;

    public EditTimeRegistrationSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder, ActivityHolder activityHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.activityHolder = activityHolder;
        this.project = projectHolder.getProject();
        this.activity = activityHolder.getActivity();
    }

    @Given("the user {string} has a time registration with {string}")
    public void theUserHasATimeRegistrationWith(String userInitials, String activityName) throws Exception {
        User user = timeManager.getUserFromInitials(userInitials);
        Activity registeredActivity = activityHolder.getActivity();
        this.timeRegistration = new TimeRegistration(user, registeredActivity, 8, LocalDate.now());
        user.addTimeRegistration(this.timeRegistration);
    }
    @When("the user changes the registered user on the time registration to {string}")
    public void theUserChangesTheRegisteredUserOnTheTimeRegistrationTo(String userInitials) {
        User user = timeManager.getUserFromInitials(userInitials);
        timeRegistration.setRegisteredUser(user);
    }
    @Then("the registered user on the time registration is {string}")
    public void theRegisteredUserOnTheTimeRegistrationIs(String userInitials) {
        assertEquals(timeRegistration.getRegisteredUser().getUserInitials(), userInitials);
    }
    @Then("the user {string} has not contributed to the activity")
    public void theUserHasNotContributedToTheActivity(String userInitials) {
        User user = timeManager.getUserFromInitials(userInitials);
        assertFalse(this.activity.getContributingUsers().contains(user));
    }
    @Then("the user {string} has contributed to the activity")
    public void theUserHasContributedToTheActivity(String userInitials) {
        User user = timeManager.getUserFromInitials(userInitials);
        assertTrue(this.activity.getContributingUsers().contains(user));
    }
}
