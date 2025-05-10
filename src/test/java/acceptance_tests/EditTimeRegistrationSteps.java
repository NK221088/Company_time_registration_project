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
    @Given("the user {string} has {int} time registration with {string}")
    public void theUserHasTimeRegistrationWith(String userInitials, Integer numberOfActivities, String string2) throws Exception {
        User user = timeManager.getUserFromInitials(userInitials);
        Activity registeredActivity = activityHolder.getActivity();
        this.timeRegistration = new TimeRegistration(user, registeredActivity, 8, LocalDate.now());
        assertEquals(user.getActivityRegistrations().get(registeredActivity).size(), numberOfActivities);
    }
    @Given("there are registered {int} work hours on the project")
    public void thereAreRegisteredWorkHoursOnTheProject(Integer registeredHours) throws Exception {
        this.timeRegistration = new TimeRegistration(timeManager.getCurrentUser(), this.activity, registeredHours, LocalDate.now());
        Double workedHours = this.activity.getWorkedHours();
        assertEquals(workedHours, registeredHours, 0.0001);
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
    @Given("the registered date is {string}")
    public void theRegisteredDateIs(String newDate) throws Exception {
        try {timeRegistration.setRegisteredDate(LocalDate.parse(newDate));}
        catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @When("the user changes the registered date to {string}")
    public void theUserChangesTheRegisteredDateTo(String newDate) {
        try {
            timeRegistration.setRegisteredDate(LocalDate.parse(newDate));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @When("the user changes the registered hours to {int}")
    public void theUserChangesTheRegisteredHoursTo(Integer newHours) {
        timeRegistration.setRegisteredHours(newHours);
    }
    @Then("the registered date is changed to {string}")
    public void theRegisteredDateIsChangedTo(String newDate) {
        assertEquals(timeRegistration.getRegisteredDate().toString(), newDate);
    }
    @Then("the registered hours is changed to {int}")
    public void theRegisteredHoursIsChangedTo(Integer newHours) {
        Double registeredHours = timeRegistration.getRegisteredHours();
        assertEquals(registeredHours, newHours, 0.0001);
    }
}
