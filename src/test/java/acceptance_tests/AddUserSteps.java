package acceptance_tests;


import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class AddUserSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    User user;

    public AddUserSteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @When("a new user with initials {string} is added to the system")
    public void aNewUserWithInitialsIsAddedToTheSystem(String userInitials) {
        try {
            this.user = new User(userInitials);
            timeManager.addUser(user);

        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the new user is registered in the system")
    public void theNewUserIsRegisteredInTheSystem() {
        assertTrue(timeManager.getUsers().contains(user));
    }
    @Then("the new user is not registered in the system again")
    public void theNewUserIsNotRegisteredInTheSystemAgain() {
        long count = timeManager.getUsers().stream()
                .filter(us -> us.getUserInitials().equals(user.getUserInitials()))
                .count();
        assertTrue(count == 1);
    }
    @Then("the new user is not registered in the system")
    public void theNewUserIsNotRegisteredInTheSystem() {
        assertFalse(timeManager.getUsers().contains(user));
    }
}
