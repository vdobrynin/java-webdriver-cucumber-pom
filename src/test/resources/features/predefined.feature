@predefined
Feature: Smoke steps

  @predefined1
  Scenario: Open page and verify title
    Given I open local page "predefined.html"
    Then I should see page title as "Predefined Test Page"

  @predefined2
  Scenario: Verify page title contains text
    Given I open local page "predefined.html"
    Then I should see page title contains "Test Page"

  @predefined3
  Scenario: Type into a field
    Given I open local page "predefined.html"
    Then element with xpath "//input[@name='q']" should be present
    When I type "Cucumber" into element with xpath "//input[@name='q']"
    Then element with xpath "//input[@name='q']" should have attribute "value" as "Cucumber"

  @predefined4
  Scenario: Submit search with a button
    Given I open local page "predefined.html"
    When I type "Cucumber" into element with xpath "//input[@name='q']"
    Then I click on element with xpath "//button[@id='search-button']"
    Then I wait for element with xpath "//div[@id='results']" to be present
    Then element with xpath "//div[@id='results']" should contain text "Cucumber"

  @predefined5
  Scenario: Submit search using JavaScript
    Given I open local page "predefined.html"
    When I type "BDD" into element with xpath "//input[@name='q']"
    Then I click on element using JavaScript with xpath "//button[@id='search-button']"
    Then I wait for element with xpath "//div[@id='results']" to be present
    Then element with xpath "//div[@id='results']" should contain text "BDD"

  @predefined6
  Scenario: Clear a field
    Given I open local page "predefined.html"
    When I type "Cucumber" into element with xpath "//input[@name='q']"
    And I clear element with xpath "//input[@name='q']"
    Then element with xpath "//input[@name='q']" should have attribute "value" as ""

  @predefined7
  Scenario: Verify element visibility
    Given I open local page "predefined.html"
    Then element with xpath "//div[@id='message']" should be displayed
    And element with xpath "//div[@id='hidden-element']" should not be displayed
    And element with xpath "//div[@id='does-not-exist']" should not be present

  @predefined8
  Scenario: Verify element state
    Given I open local page "predefined.html"
    Then element with xpath "//input[@id='agree']" should be enabled
    And element with xpath "//button[@id='disabled-button']" should be disabled
    When I click on element with xpath "//input[@id='agree']"
    Then element with xpath "//input[@id='agree']" should be selected

  @predefined9
  Scenario: Resize window
    Given I open local page "predefined.html"
    When I resize window to 800 and 600
    Then element with xpath "//div[@id='message']" should be displayed

  @predefined10
  Scenario: Work with iframe
    Given I open local page "predefined.html"
    When I switch to iframe with xpath "//iframe[@id='frame']"
    Then element with xpath "//p[@id='frame-text']" should contain text "Inside frame"
    And I switch to default content
    Then element with xpath "//div[@id='message']" should be displayed

  @predefined11
  Scenario: Work with alerts
    Given I open local page "predefined.html"
    When I click on element with xpath "//button[@id='alert-button']"
    Then I accept alert
    When I click on element with xpath "//button[@id='alert-button']"
    Then I dismiss alert
