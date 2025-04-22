package acceptance_tests;

import dtu.time_manager.app.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddTimeRegistrationSteps {
    private Activity registeredActivity;
    private int registeredHours;
    private LocalDate registeredDate;

    @When("the user selects the activity {string} in project {string}")
    public void theUserSelectsTheActivityInProject(String activityName, String projectName) {
        registeredActivity = TimeManager.getProjectFromName(projectName).getActivityFromName(activityName);
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
    public void aNewTimeRegistrationIsAddedWith(io.cucumber.datatable.DataTable dataTable) {
        TimeRegistration time_registration = new TimeRegistration(
            TimeManager.getCurrentUser(),
            registeredActivity,
            registeredHours,
            registeredDate
        );

        assertEquals(registeredActivity.getActivityName(), dataTable.cell(1, 0));
        assertEquals(Integer.toString(registeredHours), dataTable.cell(1, 1));
        assertEquals(registeredDate.toString(), dataTable.cell(1, 2));

        TimeManager.addTimeRegistration(time_registration);
    }
}