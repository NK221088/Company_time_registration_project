Feature: Mark activity as finalized
  Description: A registered user marks an activity as finalized
  Actors: User

  Scenario: Setting activity as finalized
    Given a user is logged in
    And an activity exists in a project
    When the user sets the activity as finalized
    Then the activity is marked as finalized
    And it's no longer possible to add time registrations to the activity

  Scenario: Setting an activity as not finalized progress
    Given a user is logged in
    And an activity exists in a project
    And the activity is set as finalized
    When the user sets the activity as not finalized
    Then the activity is not set as finalized
    And it's possible to add time registrations to the activity again