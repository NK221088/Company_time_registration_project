package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Nikolai Kuhl
public class EditProjectSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;
    private ActivityHolder activityHolder;
    private Activity activity;
    private String oldName;

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
            LocalDate date = project.getStartDate();
            String oldDate;
            if (date == null) {
                oldDate = "";
            } else {
                oldDate = date.toString();
            }
            this.projectHolder.setOldDate(oldDate);
            this.projectHolder.setOldDate(project.getStartDate().toString());
            this.project.setProjectStartDate(LocalDate.parse(newStartDate));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user changes the project's end date to {string}")
    public void theUserChangesTheProjectSEndDateTo(String newEndDate) {
        try {
            LocalDate date = project.getStartDate();
            String oldDate;
            if (date == null) {
                oldDate = "";
            } else {
                oldDate = date.toString();
            }
            this.projectHolder.setOldDate(oldDate);
            this.project.setProjectEndDate(LocalDate.parse(newEndDate));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the project's start date is changed")
    public void theProjectSStartDateIsChanged() {
        assertNotEquals(project.getStartDate(), this.projectHolder.getOldDate().toString());
    }
    @Then("the project's end date is changed")
    public void theProjectSEndDateIsChanged() {
        assertNotEquals(project.getEndDate(), this.projectHolder.getOldDate().toString());
    }

    @When("the user changes the project name of {string} to {string}")
    public void theUserChangesTheProjectNameOfTo(String projectName1, String projectName2) {
        this.oldName = project.getProjectName();
        try {timeManager.renameProject(this.project, projectName2);}
        catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the project name is not changed")
    public void theProjectNameIsNotChanged() {
        assertEquals(project.getProjectName(), this.oldName);
    }
    @Then("the project name is changed to {string}")
    public void theProjectNameIsChangedTo(String projectName) {
        assertEquals(this.project.getProjectName(), projectName);
    }

}