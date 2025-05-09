package acceptance_tests;

import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class AssignProjectLeadSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private Project project;
    private ProjectHolder projectHolder;
    private User user;

    public AssignProjectLeadSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
        this.user = timeManager.getCurrentUser();
    }

    @Given("and there is no current project lead of a project")
    public void andThereIsNoCurrentProjectLeadOfAProject() {
        assertNull(this.project.getProjectLead());
    }

    @When("the user is assigned the role project lead")
    public void theUserIsAssignedTheRoleProjectLead() {
        this.user = timeManager.getCurrentUser();
        this.project.setProjectLead(this.user);
    }

    @Then("the user is given the role project lead")
    public void theUserIsGivenTheRoleProjectLead() {
        assertEquals(this.user,this.project.getProjectLead());
    }

    @Given("there is a current project lead of a project")
    public void thereIsACurrentProjectLeadOfAProject() {
        this.project.setProjectLead(this.user);
        assertNotNull(this.project.getProjectLead());
    }

    @Then("The user will replace the current project lead and assume the role of project lead")
    public void theUserWillReplaceTheCurrentProjectLeadAndAssumeTheRoleOfProjectLead() {
        this.project.setProjectLead(this.user);
    }

}
