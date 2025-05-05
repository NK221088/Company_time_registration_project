Feature: Add activity
  Description: A registered user registers an activity
  Actors: User

Scenario: Successful addition of activity to project
  Given the user "huba" is logged in
  And a project, "Project 1", exists in the system
  When the user views the project
  And adds an activity named "Activity 1" to the project
  Then the activity named "Activity 1" should be added to the project

Scenario: Unsuccessful addition of activity to project because of already existing activity name
  Given the user "huba" is logged in
  And a project named "Project 1" with an activity named "Activity 1" exists in the system
  When the user views the project
  And adds an activity named "Activity 1" to the project
  Then the activity should not be added to the project
  And the error message "An activity with name 'Activity 1' already exists within 'Project 1' two activities cannot exist with the same name within the same project." is given