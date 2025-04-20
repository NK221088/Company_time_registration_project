Feature: Add activity
  Description: A registered user registers an activity
  Actors: User

Scenario: Successful addition of activity to project
  Given a user is logged in
  And a project with project ID "24000" and project name "Project 1" and time interval "01/01/2025 - 01/08/2025" exists in the system
  When the user views the project with project ID "24000"
  And adds an activity named "Activity 1" to the project named "Project 1"
  Then the activity named "Activity 1" should be added to the project named "Project 1"
  And the activity named "Activity 1" should be shown when the project named "Project 1" is viewed


#Scenario{Unsuccessful addition of activity to project because of already existing activity name (Isak)}{
#  Given a user is logged in
#  And a project with name "Project 1" exists in the system
#  And an activity exists within that project with name "Project 1"
#  When the user views the project
#  And adds an activity to the project with name "Activity 1"
#  Then the activity should not be added to the project
#  And the activity error message "An activity with name 'Project 1' already exists in the system and two projects can’t have the same name." is given