package acceptance_tests;

import dtu.timemanager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class AddTimeRegistrationSteps {
    private Activity registeredActivity;
    private int registeredHours;
    private LocalDate registeredDate;
    private double workedHours = 0;
    private TimeRegistration timeRegistration;
    private ActivityHolder activityHolder;

    private TimeManager timeManager;
    private ProjectHolder projectHolder;
    private Project project;
    private ErrorMessageHolder errorMessage;
    private Activity activity;

    public AddTimeRegistrationSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder, ActivityHolder activityHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.activityHolder = activityHolder;
        this.project = projectHolder.getProject();
    }

    @Given("that the project has an activity named {string} which is set as in progress")
    public void thatTheProjectHasAnActivityNamedWhichIsSetAsInProgress(String activityName) throws Exception {
        this.activity = new Activity(activityName);
        activityHolder.setActivity(activity);
        project.addActivity(activity);
        assertFalse(activity.getFinalized());
    }
    @When("the user starts a new time registration")
    public void theUserStartsANewTimeRegistration() throws Exception {
        this.timeRegistration = new TimeRegistration(timeManager.getCurrentUser());
    }
    @When("the user selects the activity {string}")
    public void theUserSelectsTheActivity(String activityName) throws Exception {
        this.timeRegistration.setRegisteredActivity((this.project.getActivityFromName(activityName)));
    }
    @When("the user enters {string} hours")
    public void theUserEntersHours(String activityHours) {
        this.timeRegistration.setRegisteredHours(Integer.parseInt(activityHours));
    }
    @When("the user selects the date {string}")
    public void theUserSelectsTheDate(String activityDate) {
        this.timeRegistration.setRegisteredDate(LocalDate.parse(activityDate));
    }
    @Then("a new time registration is added with:")
    public void aNewTimeRegistrationIsAddedWith(io.cucumber.datatable.DataTable dataTable) throws Exception {
        assertEquals(this.timeRegistration.getRegisteredActivity().getActivityName(),    dataTable.cell(1, 0));
        assertEquals(Integer.toString((int) this.timeRegistration.getRegisteredHours()), dataTable.cell(1, 1));
        assertEquals(this.timeRegistration.getRegisteredDate().toString(),               dataTable.cell(1, 2));
        timeManager.addTimeRegistration(timeRegistration);
        timeRegistration.getRegisteredUser().addTimeRegistration(timeRegistration);
    }
    @Given("that the project has an activity named {string} which is set as finalized")
    public void thatTheProjectHasAnActivityNamedWhichIsSetAsFinalized(String activityName) throws Exception {
        this.activity = new Activity(activityName);
        project.addActivity(activity);
        activity.setActivityAsFinalized();
        assertTrue(activity.getFinalized());
    }
    @Then("the time registration is not created")
    public void theTimeRegistrationIsNotCreated() {
        Activity activity = timeManager.getProjects().getFirst().getActivities().getFirst();
        assertEquals(workedHours, activity.getWorkedHours());
    }

    @Then("the hours worked on the activity is {int}")
    public void theHoursWorkedOnTheActivityIs(Integer workedHours) {
        assertEquals(this.activity.getWorkedHours(), Double.valueOf(workedHours));
    }
}