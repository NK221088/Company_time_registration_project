package acceptance_tests;

import dtu.time_manager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class AddIndependentActivitySteps extends TestBase {
    private ErrorMessageHolder errorMessage;
    private Activity activity;

    public AddIndependentActivitySteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a user with initials {string} is logged in")
    public void aUserWithInitialsIsLoggedIn(String userInitials) {
        try {
            timeManager.login(userInitials);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user creates an independent activity with name {string}")
    public void theUserCreatesAnIndependentActivityWithName(String activityName) {
        try {
            timeManager.createActivity(activityName);
            this.activity = timeManager.getActivities().stream()
                    .filter(a -> a.getName().equals(activityName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Activity not found"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the independent activity is created")
    public void theIndependentActivityIsCreated() {
        assertNotNull(this.activity, "Activity should be created");
        assertTrue(this.activity.isIndependent(), "Activity should be independent");
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
