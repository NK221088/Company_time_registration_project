package acceptance_tests;

import dtu.time_manager.app.domain.TimeManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LogoutSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;

    public LogoutSteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @When("the user {string} logs out")
    public void theUserLogsOut(String string) {
        timeManager.logout();
        assertNull(timeManager.getCurrentUser());
    }
    @Then("they are logged out")
    public void theyAreLoggedOut() {
        assertNull(timeManager.getCurrentUser());
    }
}
