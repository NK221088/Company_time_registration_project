Feature: Add Time Registration
  Description: A registered user registers their time
  Actors: User

  Scenario: Register a new Time Registration
    Given a user is logged in
    And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
    And that the project with project ID "25001" has an activity named "Activity 1"
    When the user selects the activity "Activity 1" in project "Project 1"
    And the user enters "4" hours
    And the user selects the date "2025-04-20"
    Then a new Time Registration is added with:
      | activity       | hours | date       |
      | Activity 1     | 4     | 2025-04-20 |