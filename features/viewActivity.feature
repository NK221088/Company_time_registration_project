Feature: View Activity
  Description: A registered user views an existing activity
  Actors: User

  Scenario: A user views an activity
    Given the user "huba" is logged in
    And a project named "Project 1" with an activity named "Activity 1" exists in the system
    And the activity has the start date "2025-01-11" and end date "2025-01-25"
    When the user views the project
    And select an activity with name "Activity 1" from project ID "25001"
    Then the activity name of "Activity 1" is shown
    And the expected hours of "5" hours in "Activity 1" is shown
    And the number of work hours of "3" hours is spent on "Activity 1" is shown
    And the start date is shown
    And the end date is shown
    And the initials of the developer or developers "huba" working on the "Activity 1" is shown
    And the assigned users are shown
    And the users who have worked on the project are shown


