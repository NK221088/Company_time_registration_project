Feature: Add project lead
  Description: A registered user is assigned the role project lead
  Actors: User

  Scenario: A user is assigned the role project lead without any current project lead
    Given the user "huba" is logged in
    And a project, "projectLeadProject", exists in the system
    And and there is no current project lead of a project
    When the user is assigned the role project lead
    Then the user is given the role project lead


  Scenario: A user is assigned the role project lead with current project lead
    Given the user "huba" is logged in
    And a project, "projectLeadProject", exists in the system
    And there is a current project lead of a project
    When the user is assigned the role project lead
    Then The user will replace the current project lead and assume the role of project lead



