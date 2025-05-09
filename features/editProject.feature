Feature: Editing already existing projects
  Description: A user edit a project in the system
  Actors: User

  Scenario: Successful addition of time interval for a project
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has no defined time interval
    When the user changes the project's end date to "2025-01-10"
    And the user changes the project's start date to "2025-01-30"
    Then the project's start date is changed
    And the project's end date is changed

  Scenario: Successful edit of time interval for a project
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has the start date "2025-01-11" and end date "2025-01-25"
    When the user changes the project's start date to "2025-01-20"
    Then the project's start date is changed

  Scenario: Unsuccessful edit of start time for a project
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has the start date "2025-01-11" and end date "2025-01-25"
    When the user changes the project's start date to "2025-01-30"
    Then the error message "The start date of the project can't be after the end date of the project." is given

  Scenario: Unsuccessful edit of end time for a project
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has the start date "2025-01-11" and end date "2025-01-25"
    When the user changes the project's end date to "2025-01-10"
    Then the error message "The end date of the project can't be before the start date of the project." is given

  Scenario: Successful edit of name for a project
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    When the user changes the project name of "Project 1" to "Project 2"
    Then the project name is changed to "Project 2"

  Scenario: Unsuccessful edit of name for a project
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And a project, "Project 2", exists in the system
    When the user changes the project name of "Project 1" to "Project 2"
    Then the error message "A project with name 'Project 2' already exists and two projects cannot exist with the same name." is given