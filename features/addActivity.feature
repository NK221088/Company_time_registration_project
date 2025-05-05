Feature: Add activity
  Description: A registered user registers an activity
  Actors: User

Scenario: Successful addition of activity to project
  Given the user "huba" is logged in
  And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
  When the user views the project with project ID "25001"
  And adds an activity named "Activity 1" to the project named "Project 1"
  Then the activity named "Activity 1" should be added to the project named "Project 1"
  And the activity named "Activity 1" should be shown when the project named "Project 1" is viewed


Scenario: Unsuccessful addition of activity to project because of already existing activity name
  Given the user "huba" is logged in
  And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
  And that the project with project ID "25001" have a registered activity with name "Activity 1"
  When the user views the project with project ID "25001"
  And adds an activity named "Activity 1" to the project named "Project 1"
  Then the activity should not be added to the project
  And the activity error message "An activity with name 'Activity 1' already exists within 'Project 1' two activities cannot exist with the same name within the same project." is given