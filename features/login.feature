Feature: Login
  Description: A registered user logs in
  Actors: User

  Scenario: A registered user logs in
    Given the user "huba" is registered
    When the user types in their initials "huba"
    Then they are logged into the system

  Scenario: An unregistered user attempts to log in
    When the user types in their initials "huno"
    Then they aren't logged into the system
    And the error message "The user huno don't exist in the system." is given
