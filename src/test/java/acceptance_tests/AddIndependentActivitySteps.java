package acceptance_tests;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.Activity;
import static org.junit.jupiter.api.Assertions.*;

public class AddIndependentActivitySteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;

    public AddIndependentActivitySteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @When("the user adds an independent activity named {string}")
    public void theUserAddsAnIndependentActivityNamed(String activityName) throws Exception {
        try {
            timeManager.addIndependentActivity(new Activity(activityName));
        } catch (Exception e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the independent activity is added to the project")
    public void theIndependentActivityIsAddedToTheProject() {
        assertFalse(timeManager.getIndependentActivities().isEmpty());
    }
    @Given("an independent activity named {string} exists")
    public void anIndependentActivityNamedExists(String activityName) {
        try {
            timeManager.addIndependentActivity(new Activity(activityName));
        } catch (Exception e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the independent activity isn't added to the project")
    public void theIndependentActivityIsnTAddedToTheProject() {
        assertEquals(1, timeManager.getIndependentActivities().size());
    }
    @Then("the independent activity error message {string} is given")
    public void theIndependentActivityErrorMessageIsGiven(String errorMessage) {
        assertEquals(this.errorMessage.getErrorMessage(), errorMessage);
    }
}
