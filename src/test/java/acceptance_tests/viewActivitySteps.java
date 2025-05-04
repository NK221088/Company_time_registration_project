package acceptance_tests;

import dtu.time_manager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

public class viewActivitySteps extends TestBase {
    private ErrorMessageHolder errorMessage;
    private Project testProject;
    private Activity activity;
    private User workingUser;
    private User assignedUser;
    private TimeRegistration testTimeRegistration;
    private double workedHours;
    private LocalDate startDate;
    private LocalDate endDate;

    public viewActivitySteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("the activity has the start date {string} and end date {string}")
    public void theActivityHasTheStartDateAndEndDate(String startDate, String endDate) {
        this.startDate = LocalDate.parse(startDate);
        this.endDate = LocalDate.parse(endDate);
    }

    @When("select an activity with name {string} from project ID {string}")
    public void selectAnActivityWithNameFromProjectID(String activityName, String projectId) {
        try {
            this.testProject = timeManager.getProjects().stream()
                    .filter(p -> p.getProjectId().equals(projectId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            this.activity = testProject.getActivities().stream()
                    .filter(a -> a.getName().equals(activityName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Activity not found"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the activity name of {string} is shown")
    public void theActivityNameOfIsShown(String activityName) {
        Map<String, Object> info = timeManager.getActivityInfo(this.activity);
        assertEquals(activityName, info.get("Name"));
    }

    @Then("the expected hours of {string} hours in {string} is shown")
    public void theExpectedHoursOfHoursInIsShown(String expectedHours, String activityName) {
        try {
            timeManager.setActivityExpectedHours(this.activity, Double.parseDouble(expectedHours));
            Map<String, Object> info = timeManager.getActivityInfo(this.activity);
            assertEquals(Double.parseDouble(expectedHours), info.get("ExpectedWorkHours"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the number of work hours of {string} hours is spent on {string} is shown")
    public void theNumberOfWorkHoursOfHoursIsSpentOnIsShown(String assignedWorkHours, String activityName) {
        try {
            timeManager.addUser(new User("huba"));
            this.workingUser = timeManager.getUsers().stream()
                    .filter(u -> u.getUserInitials().equals("huba"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            timeManager.registerTime(this.activity, Double.parseDouble(assignedWorkHours), LocalDate.now());
            Map<String, Object> info = timeManager.getActivityInfo(this.activity);
            assertEquals(Double.parseDouble(assignedWorkHours), info.get("WorkedHours"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the start date is shown")
    public void theStartDateIsShown() {
        try {
            timeManager.setActivityStartDate(this.activity, this.startDate);
            Map<String, Object> info = timeManager.getActivityInfo(this.activity);
            assertEquals(this.startDate, info.get("StartTime"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the end date is shown")
    public void theEndDateIsShown() {
        try {
            timeManager.setActivityEndDate(this.activity, this.endDate);
            Map<String, Object> info = timeManager.getActivityInfo(this.activity);
            assertEquals(this.endDate, info.get("EndTime"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the initials of the developer or developers {string} working on the {string} is shown")
    public void theInitialsOfTheDeveloperOrDevelopersWorkingOnTheIsShown(String userInitials, String activityName) {
        try {
            timeManager.addUser(new User(userInitials));
            this.assignedUser = timeManager.getUsers().stream()
                    .filter(u -> u.getUserInitials().equals(userInitials))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            timeManager.assignUserToActivity(this.activity, this.assignedUser);
            Map<String, Object> info = timeManager.getActivityInfo(this.activity);
            List<User> userList = (List<User>) info.get("Assigned employees");
            assertTrue(userList.contains(this.assignedUser));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the assigned users are shown")
    public void theAssignedUsersAreShown() {
        Map<String, Object> info = timeManager.getActivityInfo(this.activity);
        assertNotNull(info.get("Assigned employees"));
    }

    @Then("the users who have worked on the project are shown")
    public void theUsersWhoHaveWorkedOnTheProjectAreShown() {
        Map<String, Object> info = timeManager.getActivityInfo(this.activity);
        assertNotNull(info.get("Contributing employees"));
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
