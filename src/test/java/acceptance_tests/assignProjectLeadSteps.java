package acceptance_tests;

import dtu.time_manager.domain.Project;
import dtu.time_manager.domain.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class assignProjectLeadSteps extends TestBase {
    private Project projectLeadProject;
    private User projectLead;
    private ErrorMessageHolder errorMessage;

    public assignProjectLeadSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a User named {string} and a project named {string}")
    public void aUserNamedAndAProjectNamed(String username, String projectName) {
        try {
            timeManager.addUser(new User(username));
            timeManager.createProject(projectName);
            
            this.projectLead = timeManager.getUsers().stream()
                    .filter(u -> u.getUserInitials().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            this.projectLeadProject = timeManager.getProjects().stream()
                    .filter(p -> p.getName().equals(projectName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("and there is no current project lead of a project")
    public void andThereIsNoCurrentProjectLeadOfAProject() {
        assertNull(this.projectLeadProject.getProjectLead());
    }

    @When("the user is assigned the role project lead")
    public void theUserIsAssignedTheRoleProjectLead() {
        try {
            timeManager.assignProjectLead(this.projectLeadProject, this.projectLead);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the user is given the role project lead")
    public void theUserIsGivenTheRoleProjectLead() {
        Project updatedProject = timeManager.getProjects().stream()
                .filter(p -> p.getName().equals(this.projectLeadProject.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        assertEquals(this.projectLead, updatedProject.getProjectLead());
    }

    @Given("and there is a current project lead of a project")
    public void andThereIsACurrentProjectLeadOfAProject() {
        try {
            timeManager.assignProjectLead(this.projectLeadProject, this.projectLead);
            assertNotNull(this.projectLeadProject.getProjectLead());
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("The user will replace the current project lead and assume the role of project lead")
    public void theUserWillReplaceTheCurrentProjectLeadAndAssumeTheRoleOfProjectLead() {
        try {
            timeManager.assignProjectLead(this.projectLeadProject, this.projectLead);
            
            Project updatedProject = timeManager.getProjects().stream()
                    .filter(p -> p.getName().equals(this.projectLeadProject.getName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            assertEquals(this.projectLead, updatedProject.getProjectLead());
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
