Feature: Assign activity
  Description: A user assigns someone (including themselves) to an activity
  Actors: User

  Scenario Outline: Successful assignment increases count by 1
    Given the user "<actor>" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the user "<target>" has <start_count> assigned activities
    When the user "<actor>" assigns the user "<target>" to "Activity 1" in "Project 1"
    Then the user "<target>" is assigned to "Activity 1" in "Project 1"
    And the user "<target>"'s count of currently assigned activities is <end_count>

    Examples:
      | actor | target | start_count | end_count |
      | huba  | alex   | 0           | 1         |
      | huba  | huba   | 0           | 1         |

  Scenario: Cannot assign someone past their max activities
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the user "isak" has 20 assigned activities
    When the user "huba" assigns the user "isak" to "Activity 1" in "Project 1"
    Then the user "isak" isn't assigned to "Activity 1" in "Project 1"
    And the error message "'isak' is already assigned to the maximum number of 20 activities" is given

  Scenario: Cannot assign someone to the same activity twice
    Given the user "huba" is logged in
    And a project, "Project 1", exists in the system
    And the project has an activity named "Activity 1"
    And the user "bria" has 1 assigned activities
    And the user "bria" is already assigned to "Activity 1" in "Project 1"
    When the user "huba" assigns the user "bria" to "Activity 1" in "Project 1"
    Then the error message "'bria' is already assigned to the activity 'Activity 1'" is given