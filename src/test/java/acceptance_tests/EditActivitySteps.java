package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class EditActivitySteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;
    private ActivityHolder activityHolder;
    private Activity activity;
    private Map<String, Object> info;

    public EditActivitySteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder, ActivityHolder activityHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
        this.activityHolder = activityHolder;
        this.activity = activityHolder.getActivity();
    }

    @When("the user changes the start date to {string}")
    public void theUserChangesTheStartDateTo(String newStartDate) {
        try {
            this.activityHolder.setOldDate(activity.getActivityStartTime().toString());
            this.activity.setActivityStartTime(LocalDate.parse(newStartDate));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @When("the user changes the end date to {string}")
    public void theUserChangesTheEndDateTo(String newEndDate) {
        try {
            this.activityHolder.setOldDate(activity.getActivityEndTime().toString());
            this.activity.setActivityEndTime(LocalDate.parse(newEndDate));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user changes the name of {string} to {string}")
    public void theUserChangesTheNameOfTo(String activityName1, String activityName2) {
        this.activity.setActivityName(activityName2);
    }
    @Then("the name is changed to {string}")
    public void theNameIsChangedTo(String activityName) {
        assertEquals(activity.getActivityName(), activityName);
    }
    @Then("the start date is changed")
    public void theStartDateIsChanged() {
        assertNotEquals(activityHolder.getOldDate(), activity.getActivityStartTime());
    }
}
