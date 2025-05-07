package acceptance_tests;

import dtu.timemanager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddTimeRegistrationSteps {
    private Activity registeredActivity;
    private int registeredHours;
    private LocalDate registeredDate;
    private double workedHours = 0;

    private TimeManager timeManager;
    private ProjectHolder projectHolder;
    private Project project;
    private ErrorMessageHolder errorMessage;
    private Activity activity;

    public AddTimeRegistrationSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
    }

    @Given("that the project has an activity named {string} which is set as in progress")
    public void thatTheProjectHasAnActivityNamedWhichIsSetAsInProgress(String activityName) throws Exception {
        this.activity = new Activity(activityName);
        project.addActivity(activity);

        assertFalse(activity.getFinalized());
    }
    @When("the user selects the activity {string} in project {string}")
    public void theUserSelectsTheActivityInProject(String activityName, String projectName) {
        registeredActivity = timeManager.getProjectFromName(projectName).getActivityFromName(activityName);
    }
    @When("the user enters {string} hours")
    public void theUserEntersHours(String activityHours) {
        registeredHours = Integer.parseInt(activityHours);
    }
    @When("the user selects the date {string}")
    public void theUserSelectsTheDate(String activityDate) {
        registeredDate = LocalDate.parse(activityDate);
    }
    @Then("a new Time Registration is added with:")
    public void aNewTimeRegistrationIsAddedWith(io.cucumber.datatable.DataTable dataTable) throws Exception {
        TimeRegistration time_registration = new TimeRegistration(
            timeManager.getCurrentUser(),
            registeredActivity,
            registeredHours,
            registeredDate
        );

        assertEquals(registeredActivity.getActivityName(), dataTable.cell(1, 0));
        assertEquals(Integer.toString(registeredHours), dataTable.cell(1, 1));
        assertEquals(registeredDate.toString(), dataTable.cell(1, 2));

        timeManager.addTimeRegistration(time_registration);
    }
    @Given("that the project has an activity named {string} which is set as finalized")
    public void thatTheProjectHasAnActivityNamedWhichIsSetAsFinalized(String activityName) throws Exception {
        this.activity = new Activity(activityName);
        project.addActivity(activity);
        activity.setActivityAsFinalized();
        assertTrue(activity.getFinalized());
    }
    @When("the user tries to add a time registration")
    public void theUserTriesToAddATimeRegistration() throws Exception {
        User user = timeManager.getCurrentUser();
        Activity activity = timeManager.getProjects().getFirst().getActivities().getFirst();
        workedHours = activity.getWorkedHours();
        double hours = 8;
        LocalDate date = LocalDate.now();
        try {
            TimeRegistration timeRegistration = new TimeRegistration(user, activity, hours, date);
            timeManager.addTimeRegistration(timeRegistration);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the time registration is not created")
    public void theTimeRegistrationIsNotCreated() {
        Activity activity = timeManager.getProjects().getFirst().getActivities().getFirst();
        assertEquals(workedHours, activity.getWorkedHours());

    }
}