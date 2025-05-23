Feature: Add project
  Description: A registered user creates a project
  Actors: User

  Scenario: Add a new project
    Given the user "huba" is logged in
    And the current year is 2025
    And the current project count is 2
    And no project with project name "Project Add" exists in the system
    When a new project with name "Project Add" is added
    Then a project named "Project Add" should exist in the system
    And the project is assigned a project id "25003"

  Scenario: Add a new project with an already existing name
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    When a new project with name "Project 1" is added
    Then the new project with name "Project 1" is not created
    And the error message "A project with name 'Project 1' already exists in the system and two projects can’t have the same name." is given