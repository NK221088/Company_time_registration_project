Feature: View Activity
  Description: A registered user views an existing activity
  Actors: User

  Scenario: A user views an activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the activity has the start date "2025-01-11" and end date "2025-01-25"
    And the activity has the expected hours "5"
    When the user views the project
    And select the activity with name "Activity 1"
    Then the name of the activity is shown
    And the expected hours is shown
    And the number of work hours of "3" hours is spent on "Activity 1" is shown
    And the start date is shown
    And the end date is shown
    And the assigned users are shown
    And the users who have worked on the activity are shown


