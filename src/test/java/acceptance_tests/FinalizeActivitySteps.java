package acceptance_tests;

import dtu.timemanager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FinalizeActivitySteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;

    private Project project;
    private Activity firstActivity;
    private Activity secondActivity;
    private User user;
    private double hours;
    private LocalDate date;
    private double registeredHours;

    public FinalizeActivitySteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @Given("two unfinalized activities exists in a project")
    public void twoUnfinalizedActivitiesExistsInAProject() throws Exception {
        this.timeManager = new TimeManager();
        this.project = timeManager.addProject("Project with finalized activity");;
        this.firstActivity = new Activity("Activity to be finalized");
        this.secondActivity = new Activity("Unfinalized activity");
        this.project.addActivity(firstActivity);
        this.project.addActivity(secondActivity);
        registeredHours = this.firstActivity.getWorkedHours();
        assertFalse(this.firstActivity.getFinalized() || this.secondActivity.getFinalized());


    }
    @When("the user sets the first activity as finalized")
    public void theUserSetsTheFirstActivityAsFinalized() {
        try {
            this.project.setActivityAsFinalized(this.firstActivity); // If no exception is thrown, the option is available
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the activity is set as finalized")
    public void theActivityIsMarkedAsFinalized() {
        assertTrue(this.firstActivity.getFinalized());
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
            TimeRegistration timeRegistration = new TimeRegistration(this.user, this.firstActivity, hours, date);
            timeManager.addTimeRegistration(timeRegistration);
            assertEquals(registeredHours, this.firstActivity.getWorkedHours());
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }

    }
    @Given("a finalized activity exists in a project")
    public void aFinalizedActivityExistsInAProject() throws Exception {
        this.project = timeManager.addProject("Project with finalized activity");
        this.firstActivity = new Activity("Activity to be finalized");
        this.firstActivity.setActivityAsFinalized();
        this.secondActivity = new Activity("Unfinalized activity");

        this.project.addActivity(firstActivity);
        this.project.addActivity(secondActivity);

        assertTrue(this.firstActivity.getFinalized());
    }
    @Given("an unfinalized activity exists in the project")
    public void anUnfinalizedActivityExistsInTheProject() {
        assertFalse(this.secondActivity.getFinalized());
    }
    @When("the user sets the finalized activity as unfinalized")
    public void theUserSetsTheFinalizedActivityAsUnfinalized() {
        try {
            this.project.setActivityAsUnFinalized(this.firstActivity); // If no exception is thrown, the option is available
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }

    }
    @Then("the activity is set as unfinalized")
    public void theActivityIsSetAsUnfinalized() {
        assertFalse(this.firstActivity.getFinalized());
    }
    @Then("it's possible to add time registrations to the activity")
    public void itSPossibleToAddTimeRegistrationsToTheActivity() {
        this.hours = this.firstActivity.getWorkedHours();
        try {
            TimeRegistration timeRegistration = new TimeRegistration(this.user, this.firstActivity, hours, date);
            timeManager.addTimeRegistration(timeRegistration);
            assertNotEquals(registeredHours, this.firstActivity.getWorkedHours());
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @When("the user sets the unfinalized activity as finalized")
    public void theUserSetsTheUnfinalizedActivityAsFinalized() {
        try {
            this.project.setActivityAsFinalized(this.secondActivity); // If no exception is thrown, the option is available
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project is set as finalized")
    public void theProjectIsSetAsFinalized() {
        assertTrue(this.project.getFinalized());
    }

}
