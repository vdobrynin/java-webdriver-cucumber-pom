@quote_pre
Feature: Quote Predefined

  @quote_pre1
  Scenario: Responsive behavior
    Given I open url "https://skryabin.com/market/quote.html"
    When I resize window to 1024 and 1000
    Then element with xpath "//b[@id='location']" should be displayed
    And element with xpath "//b[@id='currentDate']" should be displayed
    And element with xpath "//b[@id='currentTime']" should be displayed
#    And I wait for 1 sec
    When I resize window to 800 and 1000
    Then element with xpath "//b[@id='location']" should be displayed
    And element with xpath "//b[@id='currentDate']" should be displayed
    And element with xpath "//b[@id='currentTime']" should be displayed
#    And I wait for 1 sec
    When I resize window to 600 and 1000
    Then element with xpath "//b[@id='location']" should not be displayed
    And element with xpath "//b[@id='currentDate']" should not be displayed
    And element with xpath "//b[@id='currentTime']" should not be displayed
#    And I wait for 1 sec

  @quote_pre2
  Scenario: Username field length
    Given I open url "https://skryabin.com/market/quote.html"
    When I type "a" into element with xpath "//input[@name='username']"
    And I click on element using JavaScript with xpath "//button[@id='formSubmit']"
    Then element with xpath "//label[@id='username-error']" should contain text "2 characters"
#    And I wait for 1 sec
    When I type "b" into element with xpath "//input[@name='username']"
    Then element with xpath "//label[@id='username-error']" should not be displayed
#    And I wait for 1 sec

  @quote_pre3
  Scenario: Email field requirements
    Given I open url "https://skryabin.com/market/quote.html"
    When I type "slavaskryabin.com" into element with xpath "//input[@name='email']"
    And I click on element using JavaScript with xpath "//button[@id='formSubmit']"
    Then element with xpath "//label[@id='email-error']" should contain text "enter a valid email"
#    And I wait for 1 sec
    When I clear the field "//input[@name='email']"
    When I type "slava@skryabin.com" into element with xpath "//input[@name='email']"
    Then element with xpath "//label[@id='email-error']" should not be displayed
#    And I wait for 1 sec

  @quote_pre4
  Scenario: Email field requirements 2
    Given I open url "https://skryabin.com/market/quote.html"
    When I type "@skryabin.com" into element with xpath "//input[@name='email']"
    And I click on element using JavaScript with xpath "//button[@id='formSubmit']"
    Then element with xpath "//label[@id='email-error']" should contain text "enter a valid email"
#    And I wait for 1 sec
    When I clear element with xpath "//input[@name='email']"
    When I type "slava@skryabin.com" into element with xpath "//input[@name='email']"
    Then element with xpath "//label[@id='email-error']" should not be displayed
#    And I wait for 1 sec

  @quote_pre5
  Scenario: Password field requirements
    Given I open url "https://skryabin.com/market/quote.html"
    Then element with xpath "//input[@id='confirmPassword']" should be disabled
    When I type "password" into element with xpath "//input[@id='password']"
    Then element with xpath "//input[@id='confirmPassword']" should be enabled
#    And I wait for 1 sec

  @quote_pre6
  Scenario: Name field requirements
    Given I open url "https://skryabin.com/market/quote.html"
    When I click on element with xpath "//input[@id='name']"
    Then element with xpath "//div[@aria-describedby='nameDialog']" should be displayed
    When I type "Slava" into element with xpath "//input[@id='firstName']"
    And I type "Vlad" into element with xpath "//input[@id='middleName']"
    And I type "Skryabin" into element with xpath "//input[@id='lastName']"
    And I click on element with xpath "//span[text()='Save']"
    Then element with xpath "//input[@id='name']" should have attribute "value" as "Slava Vlad Skryabin"
#    And I wait for 1 sec

  @quote_pre7
  Scenario: Date of birth field
    Given I open url "https://skryabin.com/market/quote.html"
    When I click on element with xpath "//input[@id='dateOfBirth']"
    And I click on element with xpath "//select[@data-handler='selectMonth']/option[11]"
    And I click on element with xpath "//select[@data-handler='selectYear']/option[@value='1981']"
    And I click on element with xpath "//td[@data-handler='selectDay']/a[text()='17']"
    Then element with xpath "//input[@id='dateOfBirth']" should have attribute "value" as "11/17/1981"
#    And I wait for 1 sec


  @quote_pre8
  Scenario: Submit and verify the form
    Given I open url "https://skryabin.com/market/quote.html"
    When I type "skryabin" into element with xpath "//input[@name='username']"
    When I type "slava@skryabin.com" into element with xpath "//input[@name='email']"
    When I type "welcome" into element with xpath "//input[@name='password']"
    When I type "welcome" into element with xpath "//input[@name='confirmPassword']"
    When I click on element with xpath "//input[@id='name']"
    When I type "Slava" into element with xpath "//input[@id='firstName']"
    And I type "Skryabin" into element with xpath "//input[@id='lastName']"
    And I click on element with xpath "//span[text()='Save']"
    And I click on element using JavaScript with xpath "//input[@name='agreedToPrivacyPolicy']"
#    And I wait for 1 sec

  @quote_pre9
  Scenario: Alert
    Given I open url "https://skryabin.com/market/quote.html"
    And I click on element with xpath "//button[@id='thirdPartyButton']"
#    And I wait for 1 sec
    And I accept alert

  @quote_pre10
  Scenario: iFrame
    Given I open url "https://skryabin.com/market/quote.html"
    And I switch to iframe with xpath "//iframe[@name='additionalInfo']"
    And I type "John Doe" into element with xpath "//input[@id='contactPersonName']"
    And I type "1234567890" into element with xpath "//input[@id='contactPersonPhone']"
#    And I wait for 1 sec
    And I switch to default content
#    And I wait for 1 sec

  @quote_pre11
  Scenario: New Window
    Given I open url "https://skryabin.com/market/quote.html"
    And I click on element with xpath "//button[contains(text(),'Related documents (click)')]"
    And I switch to new window
    Then element with xpath "//body" should contain text "Document 2"
    And I switch to first window

  @quote_pre12
  Scenario: Check all Quote Results on page
    Given I open url "https://skryabin.com/market/quote.html"
    And I click on element using JavaScript with xpath "//button[@id='formSubmit']"
    Then element with xpath "//div[@id='quotePageResult']" should contain text "skryabin"
    Then element with xpath "//div[@id='quotePageResult']" should contain text "slava@skryabin.com"
    Then element with xpath "//div[@id='quotePageResult']" should not contain text "welcome"
    Then element with xpath "//div[@id='quotePageResult']" should contain text "Slava Skryabin"
    And element with xpath "//b[@name='agreedToPrivacyPolicy']" should have text as "true"
    And element with xpath "//b[@name='password']" should have text as "[entered]"

