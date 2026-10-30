@market
Feature: Marketing application test suite

  @market1
  Scenario: Required fields end-to-end
    Given I navigate to "quote" page
    When I fill out "required" fields
    And I fill out optional fields
    And I submit the quote form
    Then I verify "required" fields
    And I verify "optional" fields
