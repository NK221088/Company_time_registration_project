package acceptance_tests;

import dtu.timemanager.domain.IntervalTimeRegistration;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;

import dtu.timemanager.domain.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

// Alexander Wittrup
public class EditIntervalTimeRegistrationSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;

    private String leaveOption = "Vacation";
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now();
    private IntervalTimeRegistration intervalTimeRegistration;

    public EditIntervalTimeRegistrationSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
    }

    @Given("the user {string} has an interval time registration")
    public void theUserHasAnIntervalTimeRegistration(String userInitials) throws Exception {
        User user = timeManager.getUserFromInitials(userInitials);
        this.intervalTimeRegistration = new IntervalTimeRegistration(user, leaveOption, startDate, endDate);
        timeManager.addIntervalTimeRegistration(this.intervalTimeRegistration);
    }
    @When("the user changes the registered user on the interval time registration to {string}")
    public void theUserChangesTheRegisteredUserOnTheIntervalTimeRegistrationTo(String userInitials) {
        User user = timeManager.getUserFromInitials(userInitials);
        this.intervalTimeRegistration.setRegisteredUser(user);
    }
    @Then("the registered user on the interval time registration is {string}")
    public void theRegisteredUserOnTheIntervalTimeRegistrationIs(String userInitials) {
        assertEquals(userInitials, this.intervalTimeRegistration.getRegisteredUser().getUserInitials());
    }


    @Given("the start date is {string}")
    public void theStartDateIs(String startDate) {
        this.intervalTimeRegistration.setStartDate(LocalDate.parse(startDate));
    }
    @When("the user changes the interval start date to {string}")
    public void theUserChangesTheIntervalStartDateTo(String startDate) {
        this.intervalTimeRegistration.setStartDate(LocalDate.parse(startDate));
    }
    @Then("the interval start date is changed to {string}")
    public void theIntervalStartDateIsChangedTo(String startDate) {
        assertEquals(startDate, this.intervalTimeRegistration.getStartDate().toString());
    }


    @Given("the end date is {string}")
    public void theEndDateIs(String endDate) {
        this.intervalTimeRegistration.setEndDate(LocalDate.parse(endDate));

    }
    @When("the user changes the interval end date to {string}")
    public void theUserChangesTheIntervalEndDateTo(String endDate) {
        this.intervalTimeRegistration.setEndDate(LocalDate.parse(endDate));

    }
    @Then("the interval end date is changed to {string}")
    public void theIntervalEndDateIsChangedTo(String endDate) {
        assertEquals(endDate, this.intervalTimeRegistration.getEndDate().toString());
    }
}
