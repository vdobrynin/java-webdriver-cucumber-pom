@rest
Feature: CRUD operations for Careers

  @rest1
  Scenario: REST API Positions CRUD
    Given I login via REST API as "recruiter"
    When I create via REST API "automation" position
    Then I verify via REST API new position is in the list
    When I update via REST API "automation" position
    Then I verify via REST API new position is updated
    When I delete via REST API new position
    Then I verify via REST API new position is deleted

  @rest2
  Scenario: REST API Candidates CRUD
    Given I login via REST API as "recruiter"
    When I create via REST API "sdet" candidate
    Then I verify via REST API new candidate is in the list
    When I update via REST API "sdet" candidate
    Then I verify via REST API new candidate is updated
    When I delete via REST API new candidate
    Then I verify via REST API new candidate is deleted
