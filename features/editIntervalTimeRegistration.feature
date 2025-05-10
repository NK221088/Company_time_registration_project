Feature: Editing already existing interval time registrations
  Description: A user edits an interval time registration in the system
  Actors: User

  Scenario: Successful edit of user on interval time registration
    Given the user "huba" is logged in
    And the user "huba" has an interval time registration
    And the user "isak" is registered
    When the user changes the registered user on the interval time registration to "isak"
    Then the registered user on the interval time registration is "isak"

  Scenario: Successful edit of start date on interval time registration
    Given the user "huba" is logged in
    And the user "huba" has an interval time registration
    And the start date is "2025-04-20"
    When the user changes the interval start date to "2025-04-25"
    Then the interval start date is changed to "2025-04-25"

  Scenario: Successful edit of end date on interval time registration
    Given the user "huba" is logged in
    And the user "huba" has an interval time registration
    And the end date is "2025-05-20"
    When the user changes the interval end date to "2025-05-25"
    Then the interval end date is changed to "2025-05-25"