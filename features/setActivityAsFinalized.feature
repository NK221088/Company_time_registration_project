Feature: Mark activity as finalized
  Description: A registered user marks an activity as finalized
  Actors: User

  Scenario: Setting activity as finalized
    Given a user is logged in
    And two unfinalized activities exists in a project
    When the user sets the first activity as finalized
    Then the activity is set as finalized
    And it's no longer possible to add time registrations to the activity

  Scenario: Setting an activity as not finalized progress
    Given a user is logged in
    And a finalized activity exists in a project
    And an unfinalized activity exists in the project
    When the user sets the finalized activity as unfinalized
    Then the activity is set as unfinalized
    And it's possible to add time registrations to the activity

  Scenario: Setting the last activity in a project as finalized progress
    Given a user is logged in
    And a finalized activity exists in a project
    And an unfinalized activity exists in the project
    When the user sets the unfinalized activity as finalized
    Then the activity is set as finalized
    And the project is set as finalized