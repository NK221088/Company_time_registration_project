package acceptance_tests;

import dtu.time_manager.app.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class setActivityAsFinalizedSteps {
    private ErrorMessageHolder errorMessage;
    TimeManager timeManager = new TimeManager();
    Project project;
    Activity activity;
    User user;
    double hours;
    LocalDate date;
    double registeredHours;

    public setActivityAsFinalizedSteps() {
        errorMessage = new ErrorMessageHolder();
    }

    @Given("an activity exists in a project")
    public void anActivityExistsInAProject() throws Exception {
        Project project = new Project("Project with finalized activity");
        timeManager.addProject(project);
        this.project = project;
        Activity activity = new Activity("Activity to be finalized");
        this.activity = activity;
        this.project.addActivity(activity);
        registeredHours = this.activity.getWorkedHours();
    }
    @When("the user sets the activity as finalized")
    public void theUserSetsTheActivityAsFinalized() {
        this.activity.setActivityAsFinalized();
    }
    @Then("the activity is marked as finalized")
    public void theActivityIsMarkedAsFinalized() {
        assertTrue(this.activity.getFinalized());
    }
    @Then("it's no longer possible to add time registrations to the activity")
    public void itSNoLongerPossibleToAddTimeRegistrationsToTheActivity() throws Exception {
        User user = new User("niko");
        timeManager.addUser(user);
        this.user = user;
        double hours = 8;
        this.hours = hours;
        LocalDate date = LocalDate.now();
        this.date = date;
        try {
            TimeRegistration timeRegistration = new TimeRegistration(this.user, activity, hours, date);
            timeManager.addTimeRegistration(timeRegistration);
            assertEquals(registeredHours, this.activity.getWorkedHours());
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }

    }

    @Given("the activity is set as finalized")
    public void theActivityIsSetAsFinalized() {
        this.activity.getFinalized();
    }

    @When("the user sets the activity as not finalized")
    public void theUserSetsTheActivityAsNotFinalized() {
        this.activity.setActivityAsNotFinalized();
    }

    @Then("the activity is not set as finalized")
    public void theActivityIsNotSetAsFinalized() {
        assertFalse(this.activity.getFinalized());
    }

    @Then("it's possible to add time registrations to the activity again")
    public void itSPossibleToAddTimeRegistrationsToTheActivityAgain() {
        try {
            TimeRegistration timeRegistration = new TimeRegistration(this.user, activity, hours, date);
            timeManager.addTimeRegistration(timeRegistration);
            assertNotEquals(registeredHours, this.activity.getWorkedHours());
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

}
