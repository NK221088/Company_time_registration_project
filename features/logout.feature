Feature: Logout
  Description: A registered user logs in
  Actors: User

  Scenario: A registered user logs out
    Given a user's initials "huba" is registered in the system
    And the user with the initials "huba" is logged in
    When the user with the initials "huba" logs out
    Then they are not logged into the system