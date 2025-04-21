Feature: Login
  Description: A registered user logs in
  Actors: User

  Scenario: A registered user logs in
    Given a user's initials "huba" is registered in the system
    When the user types in their initials "huba"
    Then they are logged into the system

  Scenario: An unregistered user attempts to log in
    Given a user's initials "huno" is not registered in the system
    When the user types in their initials "huno"
    Then they are not logged into the system
    And the error message "That user is not registered" is given
