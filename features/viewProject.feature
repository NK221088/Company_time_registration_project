Feature: View project
  Description: A registered user views an existing project}
  Actors: User

Scenario: A user views a project
  Given the user "huba" is logged in
  And a project, "Project 1", exists in the system
  And the project has the start date "2025-01-01" and end date "2025-01-08"
  And project ID "25001"
  When the user views the project
  Then the project name "Project 1" is shown
  And the project ID "25001" is shown
  And time interval "2025-01-01 - 2025-01-08" is shown
  And the activities in the project are shown
  And the option for generating a project report for the project is shown