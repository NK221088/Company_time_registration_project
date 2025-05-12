package acceptance_tests;

import dtu.timemanager.domain.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

// Alexander Wittrup
public class AddIntervalTimeRegistrationSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;

    public AddIntervalTimeRegistrationSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
    }

    private String leaveOption;
    private LocalDate startDate;
    private LocalDate endDate;
    private IntervalTimeRegistration intervalTimeRegistration;

    @When("the user selects the personal leave option {string}")
    public void theUserSelectsThePersonalLeaveOption(String leaveOption) {
        this.leaveOption = leaveOption;
    }
    @When("the user tries to add an interval time registration")
    public void theUserTriesToAddAnIntervalTimeRegistration() throws Exception {
        try {
            this.intervalTimeRegistration = new IntervalTimeRegistration(timeManager.getCurrentUser(), leaveOption, startDate, endDate);
            timeManager.addIntervalTimeRegistration(this.intervalTimeRegistration);
        } catch (Exception e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @When("the user selects the start date {string}")
    public void theUserSelectsTheStartDate(String startDate) {
        this.startDate = LocalDate.parse(startDate);
    }
    @When("the user selects the end date {string}")
    public void theUserSelectsTheEndDate(String endDate) {
        this.endDate = LocalDate.parse(endDate);
    }
    @Then("a new interval time registration is added with:")
    public void aNewIntervalTimeRegistrationIsAddedWith(io.cucumber.datatable.DataTable dataTable) {
        assertEquals(intervalTimeRegistration.getLeaveOption(), dataTable.cell(1, 0));
        assertEquals(intervalTimeRegistration.getTimeInterval(), dataTable.cell(1, 1));
    }
    @Then("the interval time registration is not created")
    public void theIntervalTimeRegistrationIsNotCreated() {
        assertTrue(timeManager.getIntervalTimeRegistrations().isEmpty());
    }
}
