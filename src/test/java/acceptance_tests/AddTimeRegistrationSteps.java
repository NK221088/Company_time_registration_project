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
    private int registeredHours;
    private LocalDate registeredDate;
    private ErrorMessageHolder errorMessage;
    private double workedHours = 0;

    public AddTimeRegistrationSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a user is logged in")
    public void aUserIsLoggedIn() {
        timeManager.addUser(new User("huba"));
        timeManager.login("huba");
        assertNotNull(timeManager.getCurrentUser());
    }

    @Given("a project with project ID {string} and project name {string} and time interval {string} exists in the system")
    public void aProjectExistsInTheSystem(String projectId, String projectName, String timeInterval) {
        timeManager.createProject(projectName);
        // Note: Project ID and time interval are now handled internally by TimeManagerService
    }

    @Given("that the project with project ID {string} has an activity named {string} which is set as in progress")
    public void thatTheProjectHasAnActivitySetAsInProgress(String projectId, String activityName) {
        Project project = timeManager.getProjects().stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        Activity activity = project.getActivities().stream()
                .filter(a -> a.getName().equals(activityName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        
        assertFalse(activity.isFinalized());
    }

    @When("the user selects the activity {string} in project {string}")
    public void theUserSelectsTheActivityInProject(String activityName, String projectName) {
        Project project = timeManager.getProjects().stream()
                .filter(p -> p.getName().equals(projectName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        registeredActivity = project.getActivities().stream()
                .filter(a -> a.getName().equals(activityName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));
    }

    @When("the user enters {string} hours")
    public void theUserEntersHours(String hours) {
        registeredHours = Integer.parseInt(hours);
    }

    @When("the user selects the date {string}")
    public void theUserSelectsTheDate(String date) {
        registeredDate = LocalDate.parse(date);
    }

    @Then("a new Time Registration is added with:")
    public void aNewTimeRegistrationIsAddedWith(io.cucumber.datatable.DataTable dataTable) {
        try {
            timeManager.registerTime(registeredActivity, registeredHours, registeredDate);
            
            // Verify the registration was added correctly
            assertEquals(registeredActivity.getName(), dataTable.cell(1, 0));
            assertEquals(Integer.toString(registeredHours), dataTable.cell(1, 1));
            assertEquals(registeredDate.toString(), dataTable.cell(1, 2));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("that the project with project ID {string} has an activity named {string} which is set as finalized")
    public void thatTheProjectHasAnActivitySetAsFinalized(String projectId, String activityName) {
        Project project = timeManager.getProjects().stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        Activity activity = project.getActivities().stream()
                .filter(a -> a.getName().equals(activityName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        
        activity.setFinalized(true);
        assertTrue(activity.isFinalized());
    }

    @When("the user tries to add a time registration")
    public void theUserTriesToAddATimeRegistration() {
        try {
            timeManager.registerTime(registeredActivity, 8, LocalDate.now());
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the time registration is not created")
    public void theTimeRegistrationIsNotCreated() {
        List<TimeRegistration> registrations = timeManager.getTimeRegistrations(registeredActivity);
        assertTrue(registrations.isEmpty());
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}