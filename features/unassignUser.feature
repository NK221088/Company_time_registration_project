Feature: Unassign user
  Description: A user is unassigned from an activity
  Actors: User

  Scenario: A user is unassigned from an activity they previously was assigned to
    Given that the user with initials: "huba" is assigned the activity: "Activity 1"
    When the user tries is unassigned from the activity
    Then the user is no longer assigned to the activity

  Scenario: A user is tried to unassigned from an activity they are not assigned to
    Given that the user with initials: "huba" is not assigned the activity: "Activity 2"
    When the user tried to be unassigned from the activity
    Then the error message "The user can not be unassigned from an activity they are not assigned to" is displayed

