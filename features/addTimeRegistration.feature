Feature: Add Time Registration
  Description: A registered user registers their time
  Actors: User

  Scenario: Register a new Time Registration
    Given a user is logged in
    And an activity exists
    When the user selects an activity
    And enters a valid number of hours
    And enters a valid date
    Then the Time Registration is added