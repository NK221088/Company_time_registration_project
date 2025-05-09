Feature: Add user
  Description: Add a new user to the system
  Actors: User

  Scenario: Successful addition of new user
    When a new user with initials "john" is added to the system
    Then the new user is registered in the system

  Scenario: Unsuccessful addition of new user because user already exists
    Given the user "john" is registered
    When a new user with initials "john" is added to the system
    Then the new user is not registered in the system again
    And the error message "A user with initials 'john' is already registered in the system, please change the initials and try again." is given

  Scenario: Unsuccessful addition of new user because the user initials are in the wrong format
    When a new user with initials "Kristian" is added to the system
    Then the new user is not registered in the system
    And the error message "The user initials must be 4 letters." is given
