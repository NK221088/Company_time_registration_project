Feature: Unassign user
  Description: A user unassigns a user from an activity
  Actors: User

  Scenario: A user is unassigned from an activity they were previously assigned to
    Given the user "huba" is registered
    And a project named "Project 1" with an activity named "Activity 1" exists in the system
    And the user "huba" is already assigned to "Activity 1" in "Project 1"
    When the user "huba" is unassigned from the activity "Activity 1"
    Then the user "huba" isn't assigned to "Activity 1" in "Project 1"

  Scenario: A user isn't unassigned from an activity because they already aren't assigned
    Given the user "huba" is registered
    And a project named "Project 1" with an activity named "Activity 1" exists in the system
    When the user "huba" is unassigned from the activity "Activity 1"
    Then the error message "The user can not be unassigned from an activity they are not assigned to" is given
