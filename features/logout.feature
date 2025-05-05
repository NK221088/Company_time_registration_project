Feature: Logout
  Description: A registered user logs in
  Actors: User

  Scenario: A registered user logs out
    Given the user "huba" is registered
    And the user "huba" is logged in
    When the user "huba" logs out
    Then they are logged out