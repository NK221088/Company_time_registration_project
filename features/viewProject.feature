Feature: View project
  Description: A registered user views an existing project}
  Actors: User

Scenario: A user views a project
  Given a user is logged in
  And a project with project ID "24000" exists in the system
  When the user views the project with project ID "24000"
  Then the project name "Project 1" is shown
  And the project ID "24000" is shown
  And time interval "01/01/2024 - 01/08/2024" is shown
  And the activities in the project with project ID "24000" is shown
  And the option for generating a project report for the project with project ID "24000" is shown