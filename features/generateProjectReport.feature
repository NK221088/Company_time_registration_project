Feature: Generate project report
  Description: A user generates a project report
  Actors: User

  Scenario: Generate project report for project
    Given a user is logged in
    And a project with project ID "25001" and project name "Project 1" and time interval "01/01/2025 - 01/08/2025" exists in the system
    And that the project with project ID "25001" has an activity named "Activity 1"
    When the user generates the project report for the project with project ID "25001"
    Then the project report is generated showing both the time spent for each activity in the project with project ID "25001" and the total time spent on the project
    And the activities' time intervals
    And the users assigned to activities in the project
    And the users who have worked on activities in the project