Feature: Generate project report
  Description: A user generates a project report
  Actors: User

  Scenario: Generate project report for project
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the project has a project lead "huba"
    When the user generates the project report for the project
    Then the project report is generated showing both the time spent for each activity in the project and the total time spent on the project
    And the activities' time intervals
    And the users assigned to activities in the project
    And the project lead
    And the users who have worked on activities in the project