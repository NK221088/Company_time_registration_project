package acceptance_tests;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.Activity;
import static org.junit.jupiter.api.Assertions.*;

public class AddIndependentActivitySteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;

    public AddIndependentActivitySteps() {
        this.timeManager = new TimeManager();
        this.errorMessage = new ErrorMessageHolder();
    }

    @When("the user adds an independent activity named {string}")
    public void theUserAddsAnIndependentActivityNamed(String activityName) {
        try {
            timeManager.addIndependentActivity(new Activity(activityName));
        } catch (Exception e) {}
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
        assertTrue(timeManager.getIndependentActivities().size() == 1);
    }
    @Then("the independent activity error message {string} is given")
    public void theIndependentActivityErrorMessageIsGiven(String errorMessage) {
        assertEquals(this.errorMessage.getErrorMessage(), errorMessage);
    }
}
