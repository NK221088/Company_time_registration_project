package acceptance_tests;

import dtu.time_manager.domain.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps extends TestBase {
    private User user;
    private String userInitials;
    private ErrorMessageHolder errorMessage;

    public LoginSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a user's initials {string} is registered in the system")
    public void aUsersInitialsIsRegisteredInTheSystem(String userInitials) {
        this.userInitials = userInitials;
        user = new User(userInitials);
    }

    @When("the user types in their initials {string}")
    public void theUserTypesInTheirInitials(String userInitials) {
        try {
            timeManager.login(userInitials);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("they are logged into the system")
    public void theyAreLoggedIntoTheSystem() {
        assertEquals(timeManager.getCurrentUser().getUserInitials(), userInitials);
    }

    @Given("a user's initials {string} is not registered in the system")
    public void aUsersInitialsIsNotRegisteredInTheSystem(String userInitials) {
        this.userInitials = userInitials;
    }

    @Then("they are not logged into the system")
    public void theyAreNotLoggedIntoTheSystem() {
        assertNotEquals(timeManager.getCurrentUser().getUserInitials(), userInitials);
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
