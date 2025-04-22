Feature: Assign activity
  Description: A user assigns someone else to an activity
  Actors: User

Scenario: A user assigns another user to an activity
  Given a user with initials "huba" and a user with initials "alex" is registered in the system
  And the user with initials "huba" is logged in
  And a project with project ID "25001" and project name "Project 1" and time interval "2025-01-01 - 2025-01-08" exists in the system
  And that the project with project ID "25001" has an activity named "Activity 1"
  When a the user with initials "huba" assigns the user with initials "alex" to an activity named "Activity 1" in the project named "Project 1"
  Then the user with initials "alex" is assigned to the activity named "Activity 1" in the project named "Project 1"
  And the user with initials "alex"'s count of assigned activities is incremented


#Scenario: A user assigns themselves user to an activity
#  Given a user is logged in
#  And that a project is created
#  And that the project is divided into activities
#  When a user assigns themselves user to an activity
#  Then that user is assigned to that activity
#  And the activity is counted towards their number of current activities
#
#
#Scenario: A user assigns another user, currently assigned to the maximum number of activities, to an activity
#  Given a user is logged in
#  And that a project is created
#  And the project is divided into activities
#  And that {user2} is currently assigned to 20 activities
#  When {user1} assigns {user2} to an activity
#  Then {user2} is not assigned to that activity
#  And the error message "{user2} is already assigned to the maximum number of 20 activities" is given
