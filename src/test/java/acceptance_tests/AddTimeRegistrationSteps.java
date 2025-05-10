package acceptance_tests;

import dtu.timemanager.domain.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class AddTimeRegistrationSteps {
    private Activity registeredActivity;
    private LocalDate registeredDate;
    private double workedHours = 0;
    private TimeRegistration timeRegistration;
    private ActivityHolder activityHolder;
    private TimeManager timeManager;
    private ProjectHolder projectHolder;
    private Project project;
    private ErrorMessageHolder errorMessage;
    private Activity activity;
    private User user;

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

    @When("the user adds a new time registration on the date {string} with activity {string} and {int} worked hours")
    public void theUserAddsANewTimeRegistrationOnTheDateWithActivityAndWorkedHours(String date, String activityName, Integer workedHours) {
        try {
            this.user = timeManager.getCurrentUser();
            this.timeRegistration = new TimeRegistration(user, activity, workedHours, LocalDate.parse(date));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("a new time registration is added with:")
    public void aNewTimeRegistrationIsAddedWith(DataTable dataTable) throws Exception {
        assertEquals(this.timeRegistration.getRegisteredActivity().getActivityName(),    dataTable.cell(1, 0));
        assertEquals(Integer.toString((int) this.timeRegistration.getRegisteredHours()), dataTable.cell(1, 1));
        assertEquals(this.timeRegistration.getRegisteredDate().toString(),               dataTable.cell(1, 2));
        assertTrue(timeManager.getTimeRegistrations().contains(this.timeRegistration));
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
    @Then("the user {string} is not added to contributing users again")
    public void theUserIsNotAddedToContributingUsersAgain(String userInitials) {
        Integer count = 0;
        for (User user : activity.getContributingUsers()) {
            if (user.getUserInitials().equals(userInitials)) {
                count++;
            }
        }
        assertFalse(count > 1);
    }
    @Then("the registered date is not changed")
    public void theRegisteredDateIsNotChanged() {
        assertEquals(activityHolder.getOldDate(), activityHolder.getTimeRegistration().getRegisteredDate().toString());
    }

}