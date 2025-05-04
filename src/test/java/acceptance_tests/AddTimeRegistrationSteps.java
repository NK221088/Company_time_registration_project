package acceptance_tests;

import dtu.time_manager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class AddTimeRegistrationSteps extends TestBase {
    private Activity registeredActivity;
    private double registeredHours;
    private LocalDate registeredDate;
    private ErrorMessageHolder errorMessage;
    private Project project;

    public AddTimeRegistrationSteps(ErrorMessageHolder errorMessage) {
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

    @Given("a project with project ID {string} and project name {string} and time interval {string} exists in the system")
    public void aProjectExistsInTheSystem(String projectId, String projectName, String timeInterval) {
        try {
            this.project = timeManager.createProject(projectName);
            assertNotNull(this.project, "Project should be created");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("that the project with project ID {string} has an activity named {string} which is set as in progress")
    public void thatTheProjectHasAnActivitySetAsInProgress(String projectId, String activityName) {
        try {
            this.project = timeManager.getProjects().stream()
                    .filter(p -> p.getProjectId().equals(projectId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            this.registeredActivity = timeManager.createActivity(activityName);
            timeManager.assignActivityToProject(this.registeredActivity, this.project);
            assertFalse(this.registeredActivity.isFinalized(), "Activity should not be finalized");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user selects the activity {string} in project {string}")
    public void theUserSelectsTheActivityInProject(String activityName, String projectName) {
        try {
            this.project = timeManager.getProjects().stream()
                    .filter(p -> p.getName().equals(projectName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            this.registeredActivity = this.project.getActivities().stream()
                    .filter(a -> a.getName().equals(activityName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Activity not found"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user enters {string} hours")
    public void theUserEntersHours(String hours) {
        this.registeredHours = Double.parseDouble(hours);
    }

    @When("the user selects the date {string}")
    public void theUserSelectsTheDate(String date) {
        this.registeredDate = LocalDate.parse(date);
    }

    @Then("a new Time Registration is added with:")
    public void aNewTimeRegistrationIsAddedWith(io.cucumber.datatable.DataTable dataTable) {
        try {
            timeManager.registerTime(this.registeredActivity, this.registeredHours, this.registeredDate);
            
            // Verify the registration was added correctly
            List<TimeRegistration> registrations = timeManager.getTimeRegistrations(this.registeredActivity);
            assertFalse(registrations.isEmpty(), "Time registration should be added");
            
            TimeRegistration registration = registrations.get(0);
            assertEquals(this.registeredActivity.getName(), dataTable.cell(1, 0), "Activity name should match");
            assertEquals(Double.toString(this.registeredHours), dataTable.cell(1, 1), "Hours should match");
            assertEquals(this.registeredDate.toString(), dataTable.cell(1, 2), "Date should match");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("that the project with project ID {string} has an activity named {string} which is set as finalized")
    public void thatTheProjectHasAnActivitySetAsFinalized(String projectId, String activityName) {
        try {
            this.project = timeManager.getProjects().stream()
                    .filter(p -> p.getProjectId().equals(projectId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            this.registeredActivity = timeManager.createActivity(activityName);
            timeManager.assignActivityToProject(this.registeredActivity, this.project);
            timeManager.setActivityAsFinalized(this.registeredActivity);
            assertTrue(this.registeredActivity.isFinalized(), "Activity should be finalized");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user tries to add a time registration")
    public void theUserTriesToAddATimeRegistration() {
        try {
            timeManager.registerTime(this.registeredActivity, 8.0, LocalDate.now());
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the time registration is not created")
    public void theTimeRegistrationIsNotCreated() {
        List<TimeRegistration> registrations = timeManager.getTimeRegistrations(this.registeredActivity);
        assertTrue(registrations.isEmpty(), "No time registrations should exist for finalized activity");
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage(), 
            "Error message should match expected message");
    }
}