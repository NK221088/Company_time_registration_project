package acceptance_tests;

import dtu.time_manager.app.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class AssignProjectLeadSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private Project projectLeadProject;
    private User projectLead;

    public AssignProjectLeadSteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @Given("a User named {string} and a project named {string}")
    public void aUserNamedAndAProjectNamed(String username, String projectName) {
        this.projectLeadProject = timeManager.createProject(projectName);
        this.projectLead = new User(username);
    }

    @Given("and there is no current project lead of a project")
    public void andThereIsNoCurrentProjectLeadOfAProject() {
        assertNull(this.projectLeadProject.getProjectLead());
    }

    @When("the user is assigned the role project lead")
    public void theUserIsAssignedTheRoleProjectLead() {
        this.projectLeadProject.assignProjectLead(this.projectLead);
    }

    @Then("the user is given the role project lead")
    public void theUserIsGivenTheRoleProjectLead() {
        assertEquals(this.projectLead,this.projectLeadProject.getProjectLead());
    }

    @Given("and there is a current project lead of a project")
    public void andThereIsACurrentProjectLeadOfAProject() {
        this.projectLeadProject.assignProjectLead(this.projectLead);
        assertNotNull(this.projectLeadProject.getProjectLead());
    }

    @Then("The user will replace the current project lead and assume the role of project lead")
    public void theUserWillReplaceTheCurrentProjectLeadAndAssumeTheRoleOfProjectLead() {
        this.projectLeadProject.assignProjectLead(this.projectLead);
    }

}
