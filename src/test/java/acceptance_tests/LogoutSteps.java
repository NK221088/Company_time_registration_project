package acceptance_tests;

import dtu.time_manager.app.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LogoutSteps {

    @Given("the user with the initials {string} is logged in")
    public void theUserWithTheInitialsIsLoggedIn(String userInitials) {
        TimeManager.login(userInitials);
        assertEquals(TimeManager.getCurrentUser(), TimeManager.getUser(userInitials));

    }

    @When("the user with the initials {string} logs out")
    public void theUserWithTheInitialsLogsOut(String string) {
        TimeManager.logout();
        assertNull(TimeManager.getCurrentUser());


    }
}
