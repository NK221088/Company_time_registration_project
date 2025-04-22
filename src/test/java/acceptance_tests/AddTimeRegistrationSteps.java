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
    private String registeredName;
    private int registeredHours;
    private LocalDate registeredDate;

    @Given("an activity named {string} exists")
    public void anActivityNamedExists(String activityName) {
//        TimeManager.getProjects().getFirst().addActivity(new Activity(activityName));
        assertEquals(TimeManager
                .getProjects().getFirst()
                .getActivities().getFirst()
                .getActivityName(), activityName);
    }
    @When("the user selects the {string} activity")
    public void theUserSelectsTheActivity(String activityName) {
        try {
        registeredName = activityName;
        } catch (Exception e) {}
    }
    @When("the user enters {string} hours")
    public void theUserEntersHours(String activityHours) {
        registeredHours = Integer.parseInt(activityHours);
    }
    @When("the user selects the date {string}")
    public void theUserSelectsTheDate(String activityDate) {
//        String[] splitDate = activityDate.split("[-]");
        registeredDate = LocalDate.parse(activityDate);
    }
    @Then("a new Time Registration is added with:")
    public void aNewTimeRegistrationIsAddedWith(io.cucumber.datatable.DataTable dataTable) {
        TimeRegistration time_registration = new TimeRegistration(
            TimeManager.getCurrentUser(),
            registeredName,
            registeredHours,
            registeredDate
        );

        assertEquals(registeredName, dataTable.cell(1, 0));
        assertEquals(Integer.toString(registeredHours), dataTable.cell(1, 1));
        assertEquals(registeredDate.toString(), dataTable.cell(1, 2));

        TimeManager.addTimeRegistration(time_registration);
    }
}