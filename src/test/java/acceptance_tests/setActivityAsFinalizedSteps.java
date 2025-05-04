package acceptance_tests;

import dtu.time_manager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class setActivityAsFinalizedSteps extends TestBase {
    private ErrorMessageHolder errorMessage;
    private Project project;
    private Activity firstActivity;
    private Activity secondActivity;
    private User user;
    private double hours;
    private LocalDate date;
    private double registeredHours;

    public setActivityAsFinalizedSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a user with initials {string} is logged in")
    public void aUserWithInitialsIsLoggedIn(String userInitials) {
        try {
            timeManager.addUser(new User(userInitials));
            timeManager.login(userInitials);
            assertNotNull(timeManager.getCurrentUser(), "User should be logged in");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("two unfinalized activities exists in a project")
    public void twoUnfinalizedActivitiesExistsInAProject() {
        try {
            // Create project and activities
            this.project = timeManager.createProject("Project with finalized activity");
            
            this.firstActivity = timeManager.createActivity("Activity to be finalized");
            this.secondActivity = timeManager.createActivity("Unfinalized activity");
            
            timeManager.assignActivityToProject(this.firstActivity, this.project);
            timeManager.assignActivityToProject(this.secondActivity, this.project);

            this.registeredHours = this.firstActivity.getWorkedHours();
            assertFalse(this.firstActivity.isFinalized(), "First activity should not be finalized");
            assertFalse(this.secondActivity.isFinalized(), "Second activity should not be finalized");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user sets the first activity as finalized")
    public void theUserSetsTheFirstActivityAsFinalized() {
        try {
            timeManager.setActivityAsFinalized(this.firstActivity);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the activity is set as finalized")
    public void theActivityIsMarkedAsFinalized() {
        assertTrue(this.firstActivity.isFinalized(), "Activity should be finalized");
    }

    @Then("it's no longer possible to add time registrations to the activity")
    public void itSNoLongerPossibleToAddTimeRegistrationsToTheActivity() {
        try {
            timeManager.addUser(new User("niko"));
            this.user = timeManager.getUsers().stream()
                    .filter(u -> u.getUserInitials().equals("niko"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            this.hours = 8.0;
            this.date = LocalDate.now();

            timeManager.registerTime(this.firstActivity, this.hours, this.date);
            assertEquals(registeredHours, this.firstActivity.getWorkedHours(), 
                "Worked hours should not change for finalized activity");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("a finalized activity exists in a project")
    public void aFinalizedActivityExistsInAProject() {
        try {
            this.project = timeManager.getProjects().stream()
                    .filter(p -> p.getName().equals("Project with finalized activity"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            this.firstActivity = this.project.getActivities().stream()
                    .filter(a -> a.getName().equals("Activity to be finalized"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Activity not found"));

            this.secondActivity = this.project.getActivities().stream()
                    .filter(a -> a.getName().equals("Unfinalized activity"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Activity not found"));

            timeManager.setActivityAsFinalized(this.firstActivity);
            assertTrue(this.firstActivity.isFinalized(), "Activity should be finalized");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("an unfinalized activity exists in the project")
    public void anUnfinalizedActivityExistsInTheProject() {
        assertFalse(this.secondActivity.isFinalized(), "Second activity should not be finalized");
    }

    @When("the user sets the finalized activity as unfinalized")
    public void theUserSetsTheFinalizedActivityAsUnfinalized() {
        try {
            timeManager.setActivityAsUnfinalized(this.firstActivity);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the activity is set as unfinalized")
    public void theActivityIsSetAsUnfinalized() {
        assertFalse(this.firstActivity.isFinalized(), "Activity should be unfinalized");
    }

    @Then("it's possible to add time registrations to the activity")
    public void itSPossibleToAddTimeRegistrationsToTheActivity() {
        try {
            this.hours = 8.0;
            this.date = LocalDate.now();
            timeManager.registerTime(this.firstActivity, this.hours, this.date);
            assertNotEquals(registeredHours, this.firstActivity.getWorkedHours(), 
                "Worked hours should change for unfinalized activity");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user sets the unfinalized activity as finalized")
    public void theUserSetsTheUnfinalizedActivityAsFinalized() {
        try {
            timeManager.setActivityAsFinalized(this.secondActivity);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project is set as finalized")
    public void theProjectIsSetAsFinalized() {
        assertTrue(this.project.isFinalized(), "Project should be finalized");
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage(), 
            "Error message should match expected message");
    }
}
