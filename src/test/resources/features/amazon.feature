@amazon
# Local fixture keeps the search test deterministic; live Amazon blocks/crashes automated sessions.
Feature: Amazon

  Scenario Outline: Amazon search
    Given I open local page "amazon.html"
    And I search amazon for "<search_text>"
    Then I verify it contains "<search_result>"
    Examples:
      | search_text          | search_result       |
      | programming          | python crash course |
      | software development | clean architecture  |
