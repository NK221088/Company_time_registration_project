Feature: Add interval time registration
  Description: A registered user registers their time as a date interval
  Actors: User

  Scenario: register a new interval time registration
    Given the user "huba" is logged in
    When the user selects the personal leave option "Vacation"
    And the user selects the start date "2025-04-20"
    And the user selects the end date "2025-05-20"
    And the user tries to add an interval time registration
    Then a new interval time registration is added with:
      | leaveOption | interval                |
      | Vacation    | 2025-04-20 - 2025-05-20 |

  Scenario: Try register a new time registration for an activity set as finalized CHANGE TRHIS NOW
    Given the user "huba" is logged in
    When the user selects the personal leave option "Vacation"
    And the user selects the start date "2025-04-20"
    And the user selects the end date "2025-05-20"
    And the user tries to add an interval time registration
    Then the interval time registration is not created
    And the error message "The activity is set as finalized: time registrations can't be added." is given