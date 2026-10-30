@quote
Feature: Quote project

  @quote1
  Scenario: Base scenario
    Given I go to "quote" page
    And I print page details
    And I go to "google" page
    And I print page details
    And I go back and forward, then refresh the page
    And I change resolution to "phone"
    And I change resolution to "desktop"
    And I change resolution to "other"

  @quote2
  Scenario: Quote end to end
    Given I go to "quote" page
    And I change resolution to "desktop"
    When I fill out required fields
    And I submit the form
    Then I verify required fields

  @quote3
  Scenario: Browser logs
    Given I go to "yahoo" page
    And I print logs to the console

  @quote4
  Scenario: Dealing with alert, frame, window
    Given I go to "quote" page
    When I "accept" 3rd party agreement
    And I fill out "John Doe" and "0987654321" in additional info
    And I verify related documents "Document 2" is present
