Feature: Assign activity
  Description: A user assigns someone else to an activity
  Actors: User

Scenario: A user assigns another user to an activity
  Given a user with initials "huba" and a user with initials "alex" is registered in the system
  And the user with initials "huba" is logged in
  And the user with initials "alex"'s count of currently assigned activities is 0
  And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
  And that the project with project ID "25001" has an activity named "Activity 1"
  When the user with initials "huba" assigns the user with initials "alex" to an activity named "Activity 1" in the project named "Project 1"
  Then the user with initials "alex" is assigned to the activity named "Activity 1" in the project named "Project 1"
  And the user with initials "alex"'s count of currently assigned activities is 1


Scenario: A user assigns themselves user to an activity
  Given a user with initials "huba" is registered in the system
  And the user with initials "huba" is logged in
  And the user with initials "huba"'s count of currently assigned activities is 0
  And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
  And that the project with project ID "25001" has an activity named "Activity 1"
  When the user with initials "huba" assigns the user with initials "huba" to an activity named "Activity 1" in the project named "Project 1"
  Then the user with initials "huba" is assigned to the activity named "Activity 1" in the project named "Project 1"
  And the user with initials "huba"'s count of currently assigned activities is 1


Scenario: A user assigns another user, currently assigned to the maximum number of activities, to an activity
  Given a user with initials "huba" and a user with initials "isak" is registered in the system
  And the user with initials "huba" is logged in
  And the user with initials "isak"'s count of currently assigned activities is 20
  And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
  And that the project with project ID "25001" has an activity named "Activity 1"
  When the user with initials "huba" assigns the user with initials "isak" to an activity named "Activity 1" in the project named "Project 1"
  Then the user with initials "isak" is not assigned to the activity named "Activity 1" in the project named "Project 1"
  And the user with initials "isak"'s count of currently assigned activities is 20
  And the error message "'isak' is already assigned to the maximum number of 20 activities" is given
