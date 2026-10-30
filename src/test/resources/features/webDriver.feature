@webDriver
Feature: WebDriver function

  @webDriver1
  Scenario: Open the page
    Given I go to "google" page and print details
    And I go to "sample" page and print details
    And I go back and forward, then refresh the page
    And I print logs to the console

  @webDriver2
  Scenario:  Loop through url
    Given I open url "https://www.google.com/" then "https://www.bing.com/" then "https://www.yahoo.com/"
    And I open urls "https://www.ups.com/us/en/Home.page" then "https://start.att.net/" then "https://yandex.com/"

  @webDriver3
  Scenario: Window sizes
    Given I go to "sample" page and print details
    And I change resolution to "phone"
    And I change resolution to "desktop"

  @webDriver4
  Scenario: Element steps
    Given I go to "sample" page and print details
    When I fill out first name "V---a" and last name "D------n"
    And I fill out required fields
    Then I verify email field behavior
    When I submit the form
    Then I verify fields saved correctly

  @webDriver5
  Scenario: Validate ZIP code for Portnov Computer School
    Given  I go to "usps" page and print details
    When  I go to Looking ZIP page by address
    And I fill out "4970 El Camino Real" street "Los Altos" city "CA"
    Then I validate "94022" zip code exists in result
