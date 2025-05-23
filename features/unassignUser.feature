Feature: Unassign user
  Description: A user unassigns a user from an activity
  Actors: User

  Scenario: A user is unassigned from an activity they were previously assigned to
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the user "huba" has 1 assigned activity, "Activity 1" in "Project 1"
    When the user "huba" is unassigned from the activity
    Then the user isn't assigned to the activity
    And the user's count of currently assigned activities is 0

  Scenario: A user isn't unassigned from an activity because they already aren't assigned
    Given the user "huba" is registered
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the user "huba" has 0 assigned activities
    And the user isn't assigned to the activity
    When the user "huba" is unassigned from the activity
    Then the error message "The user can not be unassigned from an activity they are not assigned to" is given
    And the user's count of currently assigned activities is 0
