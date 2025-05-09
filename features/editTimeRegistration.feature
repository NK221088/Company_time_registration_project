Feature: Editing already existing time registrations
  Description: A user edit a time registration in the system
  Actors: User

  Scenario: Successful edit of user on time registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And the user "huba" has contributed to the activity
    And the user "isak" has not contributed to the activity
    And there is 1 time registration on the activity
    And the registered user on the time registration is "huba"
    When the user changes the registered user to "isak"
    Then the registered user on the time registration is "isak"
    And the user "huba" has not contributed to the activity
    And the user "isak" has contributed to the activity

  Scenario: Successful edit of registered date on time registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And there is 1 time registration on the activity
    And the registered date is "2025-04-20"
    When the user changes the registered date to "2025-05-20"
    Then the registered date is changed to "2025-05-20"

  Scenario: Successful edit of registered hours on time registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And the hours worked on the activity is 4
    And there is 1 time registration on the activity
    And the registered hours is 4
    When the user changes the registered hours to 5
    Then the registered hours is changed to 5
    And the hours worked on the activity is 5