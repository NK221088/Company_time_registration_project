Feature: Add Time Registration
  Description: A registered user registers their time
  Actors: User

  Scenario: Register a new Time Registration
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as in progress
    When the user selects the activity "Activity 1" in project "Project 1"
    And the user tries to add a time registration
    And the user enters "4" hours
    And the user selects the date "2025-04-20"
    Then a new Time Registration is added with:
      | activity       | hours | date       |
      | Activity 1     | 4     | 2025-04-20 |

  Scenario: Try register a new Time Registration for an activity set as finalized
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And that the project has an activity named "Activity 1" which is set as finalized
    When the user selects the activity "Activity 1" in project "Project 1"
    And the user tries to add a time registration
    Then the time registration is not created
    And the error message "The activity is set as finalized: Time registrations can't be added." is given