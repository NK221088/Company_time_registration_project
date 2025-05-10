Feature: Add time registration
  Description: A registered user registers their time
  Actors: User

  Scenario: register a new time registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    When the user adds a new time registration on the date "2025-04-20" with activity "Activity 1" and 4 worked hours
    Then a new time registration is added with:
      | activity       | hours | date       |
      | Activity 1     | 4     | 2025-04-20 |
    And the hours worked on the activity is 4
    And the user "huba" has contributed to the activity

  Scenario: Try registering a new time registration for an activity set as finalized
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as finalized
    When the user adds a new time registration on the date "2025-04-20" with activity "Activity 1" and 4 worked hours
    Then the time registration is not created
    And the error message "The activity is set as finalized: time registrations can't be added." is given

  Scenario: register a new time registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And the user "huba" has 1 time registration with "Activity 1"
    And the hours worked on the activity is 8
    When the user adds a new time registration on the date "2025-04-20" with activity "Activity 1" and 4 worked hours
    Then a new time registration is added with:
      | activity       | hours | date       |
      | Activity 1     | 4     | 2025-04-20 |
    And the hours worked on the activity is 12
    And the user "huba" is not added to contributing users again

  Scenario: Unsuccessful future registered date on time registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    And the user "huba" has 1 time registration with "Activity 1"
    And the registered date is "2025-04-20"
    When the user changes the registered date to "2030-04-25"
    Then the registered date is not changed
    And the error message "Registered date cannot be in the future." is given
