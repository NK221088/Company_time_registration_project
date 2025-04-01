package acceptance_tests;

import dtu.example.ui.*;
import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import static org.junit.jupiter.api.Assertions.*;


public class LoginSteps {
    private User user;
    private String user_initials;
    private TimeManager time_manager;
    private ErrorMessageHolder errorMessage;

    public LoginSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
        this.time_manager = new TimeManager();
    }

    @Given("a users initials {string} is registered in the system")
    public void aUsersInitialsIsRegisteredInTheSystem(String user_initials) {
        this.user_initials = user_initials;
        user = new User(user_initials);
    }
    @When("the user types in their initials {string}")
    public void theUserTypesInTheirInitials(String user_initials) {
        TimeManager.login(user_initials);
    }

    @Then("they are logged into the system")
    public void theyAreLoggedIntoTheSystem() {
        assertEquals(TimeManager.logged_in, user_initials);
    }

    @Given("a users initials {string} is not registered in the system")
    public void aUsersInitialsIsNotRegisteredInTheSystem(String string) {
    }
    @Then("they are not logged into the system")
    public void theyAreNotLoggedIntoTheSystem() {
        assertNotEquals(TimeManager.logged_in, user_initials);
    }
    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        this.errorMessage.setErrorMessage(errorMessage);
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
