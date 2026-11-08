@usps
Feature: USPS scenarios
  Background:
    Given I go to "usps" page

  @usps1
  Scenario Outline: Validate ZIP code for Portnov Computer School
    When I go to Lookup ZIP page by address
    And I fill out "<street>" street, "<city>" city, "<state>" state
    Then I validate "<zip>" zip code exists in the result
    Examples:
      | street              | city      | state | zip   |
      | 4970 El Camino Real | Los Altos | CA    | 94022 |
      | 870 7th Ave         | New York  | NY    | 10019 |

  @usps2
  Scenario: Calculate price
    When I go to Calculate Price page
    And I select "United Kingdom (United Kingdom of Great Britain and Northern Ireland)" with "Postcard" shape
    And I define "2" quantity
    Then I calculate the price and validate cost is "$3.50"

  @usps3
  Scenario: Wrong store id does not match
    When I go to "Postal Store" tab
    And I enter "12345" into store search
    Then I search and validate no products found

  @usps4
  Scenario: Wrong store id does not match
    When I go to "Postal Store" tab
    And I enter "postcard" into store search
    Then I search and validate products found

  @usps5
  Scenario: Priority Mail
    When I go to "Stamps and Supplies" section
    And I open "Supplies" in the store
    And choose category "Priority Mail"
    Then I verify that products found in "Priority Mail" category

  @usps6
  Scenario: Verify t-shirts
    When I go to "Postal Store" tab
    And I enter "t-shirt" into store search
    Then I search and validate products found
    And I verify that "U.S. Mail T-Shirt" present in results
    And I verify that "Mr. ZIP Blue T-Shirt" present in results

  @usps7
  Scenario: Verify location
    When I go to "Find USPS Locations" page
    And I search "94043" zip within "10" miles
    Then I verify that "MOUNTAIN VIEW" present in search results
    And I verify that "Post Office™" in "MOUNTAIN VIEW" results
    And I verify that "Street Parking" in "MOUNTAIN VIEW" results
    And I verify the number of search results is shown

  @usps8
  Scenario: PO Box
    When I go to "PO Boxes" page
    And I reserve new PO box for "94022"
    Then I verify that "Los Altos — Post Office™" present
    And I verify that "Size 5" PO Box is available in "Los Altos — Post Office™"

  @usps9
  Scenario: Informed delivery enabled
    When I go to "Informed Delivery" section
    Then I verify that informed delivery is "enabled"

  @usps10
  Scenario: Informed delivery sign up is disabled without email
    When I go to "Informed Delivery" section
    And I open "Sign Up for Free" link
    Then I verify that informed delivery sign up is "disabled" without email

  @usps11
  Scenario: Sign-in required to purchase supplies
    When I go to "Postal Store" tab
    And I enter "Free Boxes" into store search
    Then I search and validate products found
    When I select product "Priority Mail Flat Rate® Medium Box"
    And I add it to the cart
    Then I validate that Sign In is required

  @usps12
  Scenario: Quadcopters delivery
    When I go to "Help" tab
    And I perform "Quadcopters delivery" help search
    Then I verify that no results of "Quadcopters delivery" available in help search

  @usps13
  Scenario: Every door direct mail
    When I go to "Every Door Direct Mail" page
    And I search for "94043" in EDDM
    And I click "Table" view on the map
    When I click "Select All" on the table
    And I close modal window
    Then I verify that summary of all rows of Cost column is equal Approximate Cost in Order Summary

