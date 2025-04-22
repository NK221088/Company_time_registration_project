Feature: View project
  Description: A registered user views an existing project}
  Actors: User

Scenario: A user views a project
  Given a user is logged in
  And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
  When the user views the project with project ID "25001"
  Then the project name "Project 1" is shown
  And the project ID "25001" is shown
  And time interval "2025-01-01 - 2025-01-08" is shown
  And the activities in the project with project ID "25001" is shown
  And the option for generating a project report for the project with project ID "25001" is shown