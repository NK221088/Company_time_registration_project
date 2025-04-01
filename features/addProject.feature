Feature: Add project
  Description: A registered user creates a project
  Actors: User

  Scenario: Add a new project
    Given a user is logged in
    When a new project with name "Project 1" is added
    Then a project named "Project 1" should exist in the system

  Scenario: Add a new project with an already existing name
    Given a user is logged in
    And a project with name "Project 1" exists in the system
    When a new project with name "Project 1" is added
    Then the project with name "Project 1" is not created
    And the project error message "A project with name 'Project 1' already exists in the system and two projects can’t have the same name." is given