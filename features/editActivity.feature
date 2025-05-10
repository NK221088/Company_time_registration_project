Feature: Editing already existing activities
  Description: A user edit an activity in the system
  Actors: User

  Scenario: Successful addition of time interval for an activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the activity has no defined time interval
    When the user changes the end date to "2025-01-10"
    And the user changes the start date to "2025-01-20"
    Then the start date is changed
    And the end end date is changed

  Scenario: Successful edit of time interval for an activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the activity has the start date "2025-01-11" and end date "2025-01-25"
    When the user changes the start date to "2025-01-20"
    Then the start date is changed

  Scenario: Unsuccessful edit of start time or an activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the activity has the start date "2025-01-11" and end date "2025-01-25"
    When the user changes the start date to "2025-01-30"
    Then the error message "The start date of the activity can't be after the start date of the activity." is given

  Scenario: Unsuccessful edit of end time for an activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the activity has the start date "2025-01-11" and end date "2025-01-25"
    When the user changes the end date to "2025-01-10"
    Then the error message "The end date of the activity can't be before the start date of the activity." is given

  Scenario: Successful edit of name for an activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    When the user changes the name of "Activity 1" to "Activity 2"
    Then the name is changed to "Activity 2"

  Scenario: Unsuccessful edit of name for an activity
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the project has an activity named "Activity 2"
    When the user changes the name of "Activity 1" to "Activity 2"
    Then the error message "An activity with name Activity 2 already exists within Project 1. Two activities cannot exist with the same name within the same project." is given