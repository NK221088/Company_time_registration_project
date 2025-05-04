package acceptance_tests;

import dtu.time_manager.domain.*;
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

    @Given("a user with initials {string} is logged in")
    public void aUserWithInitialsIsLoggedIn(String userInitials) {
        try {
            timeManager.addUser(new User(userInitials));
            timeManager.login(userInitials);
            assertNotNull(timeManager.getCurrentUser(), "User should be logged in");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("a User named {string} and a project named {string}")
    public void aUserNamedAndAProjectNamed(String username, String projectName) {
        try {
            timeManager.addUser(new User(username));
            this.projectLeadProject = timeManager.createProject(projectName);
            
            this.projectLead = timeManager.getUsers().stream()
                    .filter(u -> u.getUserInitials().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            assertNotNull(this.projectLeadProject, "Project should be created");
            assertNotNull(this.projectLead, "User should be created");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("and there is no current project lead of a project")
    public void andThereIsNoCurrentProjectLeadOfAProject() {
        assertNull(this.projectLeadProject.getProjectLead(), "Project should not have a project lead");
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
        
        assertEquals(this.projectLead, updatedProject.getProjectLead(), 
            "Project lead should be assigned correctly");
    }

    @Given("and there is a current project lead of a project")
    public void andThereIsACurrentProjectLeadOfAProject() {
        try {
            timeManager.assignProjectLead(this.projectLeadProject, this.projectLead);
            assertNotNull(this.projectLeadProject.getProjectLead(), 
                "Project should have a project lead");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("The user will replace the current project lead and assume the role of project lead")
    public void theUserWillReplaceTheCurrentProjectLeadAndAssumeTheRoleOfProjectLead() {
        try {
            User newProjectLead = new User("newLead");
            timeManager.addUser(newProjectLead);
            timeManager.assignProjectLead(this.projectLeadProject, newProjectLead);
            
            Project updatedProject = timeManager.getProjects().stream()
                    .filter(p -> p.getName().equals(this.projectLeadProject.getName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            assertEquals(newProjectLead, updatedProject.getProjectLead(), 
                "New project lead should be assigned correctly");
            assertNotEquals(this.projectLead, updatedProject.getProjectLead(), 
                "Old project lead should be replaced");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage(), 
            "Error message should match expected message");
    }
}
