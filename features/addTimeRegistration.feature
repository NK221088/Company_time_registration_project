Feature: Add Time Registration
  Description: A registered user registers their time
  Actors: User

  Scenario: Register a new Time Registration
    Given a user is logged in
    And an activity named "Activity 1" exists
    When the user selects the "Activity 1" activity
    And the user enters "4" hours
    And the user selects the date "2025-04-20"
    Then a new Time Registration is added with:
      | activity       | hours | date       |
      | Activity 1     | 4     | 2025-04-20 |