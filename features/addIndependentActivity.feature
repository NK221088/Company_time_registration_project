Feature: Add independent activity
  Description: A registered user registers an independent activity
  Actors: User

  Scenario: Successful addition of independent activity
    Given a user is logged in
    When the user adds an independent activity named "Vacation"
    Then the independent activity is added to the project

  Scenario: Unsuccessful addition of independent activity because name isn't unique
    Given a user is logged in
    And an independent activity named "Vacation" exists
    When the user adds an independent activity named "Vacation"
    Then the independent activity isn't added to the project
    And the independent activity error message "An independent activity with name 'Vacation' already exists." is given