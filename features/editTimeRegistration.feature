Feature: Editing already existing time registrations
  Description: A user edit a time registration in the system
  Actors: User

  Scenario: Successful edit of user with 1 time registration on activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And the user "huba" has 1 time registration with "Activity 1"
    And the user "isak" is registered
    When the user changes the registered user on the time registration to "isak"
    Then the registered user on the time registration is "isak"
    And the user "huba" has not contributed to the activity
    And the user "isak" has contributed to the activity

  Scenario: Successful edit of user with 2 time registration on activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And the user "huba" has 2 time registration with "Activity 1"
    And the user "isak" is registered
    When the user changes the registered user on the time registration to "isak"
    Then the registered user on the time registration is "isak"
    And the user "isak" has contributed to the activity

  Scenario: Successful edit of registered date on time registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And the user "huba" has 1 time registration with "Activity 1"
    And the registered date is "2025-04-20"
    When the user changes the registered date to "2025-04-25"
    Then the registered date is changed to "2025-04-25"

  Scenario: Successful edit of registered hours on time registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And there are registered 4 work hours on the project
    When the user changes the registered hours to 5
    Then the registered hours is changed to 5
    And the hours worked on the activity is 5

  Scenario: Try changing the registered activity on a time registration to another activity set as finalized
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And that the project has an activity named "Activity 2" which is set as finalized
    And the user "huba" has 1 time registration with "Activity 1"
    When the user changes the registered activity on the time registration to "Activity 2"
    Then the registered activity on the time registration is not changed
    And the error message "The activity is set as finalized: Time registrations can't be added." is given

  Scenario: Try changing the registered activity on a time registration to another activity set as in progress
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And the user "huba" has 1 time registration with "Activity 1"
    And that the project has an activity named "Activity 2" which is set as in progress
    When the user changes the registered activity on the time registration to "Activity 2"
    Then the registered activity on the time registration is changed