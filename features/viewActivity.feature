Feature: View Activity
  Description: A registered user views an existing activity
  Actors: User

  Scenario: A user views an activity
    Given the user "huba" is logged in
    And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
    And that the project with project ID "25001" have a registered activity with name "Activity 1"
    And the activity has the start date "2025-01-11" and end date "2025-01-25"
    When the user views the project with project ID "25001"
    And select an activity with name "Activity 1" from project ID "25001"
    Then the activity name of "Activity 1" is shown
    And the expected hours of "5" hours in "Activity 1" is shown
    And the number of work hours of "3" hours is spent on "Activity 1" is shown
    And the start date is shown
    And the end date is shown
    And the initials of the developer or developers "huba" working on the "Activity 1" is shown
    And the assigned users are shown
    And the users who have worked on the project are shown


