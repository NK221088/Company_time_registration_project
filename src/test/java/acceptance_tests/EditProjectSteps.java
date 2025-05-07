package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EditProjectSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;
    private ActivityHolder activityHolder;
    private Activity activity;
    private Map<String, Object> info;

    public EditProjectSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder, ActivityHolder activityHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
        this.activityHolder = activityHolder;
        this.activity = activityHolder.getActivity();
    }
    @When("the user changes the project's start date to {string}")
    public void theUserChangesTheProjectSStartDateTo(String newStartDate) {
        try {
            this.projectHolder.setOldDate(project.getStartDate().toString());
            this.project.setProjectStartDate(LocalDate.parse(newStartDate));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user changes the project's end date to {string}")
    public void theUserChangesTheProjectSEndDateTo(String newEndDate) {
        try {
            this.projectHolder.setOldDate(project.getEndDate().toString());
            this.project.setProjectEndDate(LocalDate.parse(newEndDate));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the project's start date is changed")
    public void theProjectSStartDateIsChanged() {
        assertNotEquals(project.getStartDate(), this.projectHolder.getOldDate().toString());
    }

}