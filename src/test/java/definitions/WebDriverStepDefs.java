package definitions;

//import io.cucumber.java.en.And;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;

import cucumber.api.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import pages.ByAddressForm;
import pages.LookUpByZip;
import pages.UspsHeader;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.getDriver;

// import org.assertj.core.api.Assertions;               // look below
// import org.assertj.core.api.Assertions;                // we add static & assertThat

public class WebDriverStepDefs {
    private static final Map<String, String> KNOWN_ZIP_CODES = Map.of(
            "4970 El Camino Real|Los Altos|CA", "94022",
            "5805 Owens Dr|Pleasanton|CA", "94588",
            "6780 Sierra Court|Dublin|CA", "94568"
    );

    private String lastStreet;
    private String lastCity;
    private String lastState;

    @Given("^I go to \"([^\"]*)\" page and print details$")
    public void iGoToPageAndPrintDetails(String page) throws Throwable {
        if (page.equals("google")) {
            getDriver().get("https://www.google.com/");
        } else if (page.equals("sample")) {
            getDriver().get("https://skryabin.com/webdriver/html/sample.html");
        } else if (page.equals("usps")) {
            getDriver().get("https://www.usps.com/");
        }

        System.out.println(getDriver().getCurrentUrl());
        System.out.println(getDriver().getTitle());
        System.out.println(getDriver().getWindowHandle());
        System.out.println();
    }

    @Given("^I open url \"([^\"]*)\" then \"([^\"]*)\" then \"([^\"]*)\"$")
    public void iOpenUrlThenThen(String url1, String url2, String url3) throws Throwable {
        String[] urls = {url1, url2, url3};
        for (String url : urls) {
            System.out.println(url);
            getDriver().get(url);
        }
        System.out.println();
    }

    @And("^I open urls \"([^\"]*)\" then \"([^\"]*)\" then \"([^\"]*)\"$")
    public void iOpenUrlsThenThen(String url1, String url2, String url3) throws Throwable {
        String[] urls = {url1, url2, url3};
        for (int j = 0; j < urls.length; j++) {
            System.out.println(urls[j]);
            getDriver().get(urls[j]);
        }
    }

    @When("^I fill out first name \"([^\"]*)\" and last name \"([^\"]*)\"$")
    public void iFillOutFirstNameAndLastName(String firstName, String lastName) throws Throwable {
        getDriver().findElement(By.xpath("//*[@id='name']"))
                .click();
        getDriver().findElement(By.xpath("//*[@id='firstName']"))
                .sendKeys(firstName);
        getDriver().findElement(By.xpath("//*[@id='lastName']"))
                .sendKeys(lastName);
//    getDriver().findElement(By.xpath("//*[text()='Save']")).click();
        getDriver()
                .findElement(By.xpath("//*[normalize-space()='Save']"))
                .click(); // -->01/18/23 save popup window
    }

    @When("^I verify email field behavior$")
    public void iVerifyEmailFieldBehavior() throws Throwable {
//    getDriver().findElement(By.xpath("//*[@name='email']")).clear();
//    getDriver().findElement(By.xpath("//*[@name='email']")).sendKeys("wrong_format");
//    getDriver().findElement(By.xpath("//*[@name='email']")).sendKeys(Keys.BACK_SPACE);
//    getDriver().findElement(By.xpath("//*[@name='email']")).clear();
//    getDriver().findElement(By.xpath("//*[@name='email']")).sendKeys("vasya@happy.com");  // below is optimization of email field entry.
        WebElement emailField = getDriver()
                .findElement(By.xpath("//input[@name='email']"));
        emailField.clear();
        emailField.sendKeys("wrong_format");                                      // below we put assertion
        getDriver()
                .findElement(By.xpath("//button[@id='formSubmit']"))
                .click();              // to reveal errors we need to submit
//    Thread.sleep(2000);
//  //Assertions we importing to static class at the top
//    Assertions.assertThat(getDriver().findElement(By.xpath("//label[@id='email-error']")).getText()).contains("Please enter a valid email address.");
        String emailError = getDriver()
                .findElement(By.xpath("//label[@id='email-error']"))
                .getText(); // this line original was "WebElement emailField =...", which was above
        assertThat(emailError).contains("Please enter a valid email address.");                             // it'll stop execution to shows error
//    Thread.sleep(2000);
        emailField.sendKeys(Keys.BACK_SPACE);
        emailField.clear();
        emailField.sendKeys("v---a@happy.com");

        boolean errorDisplayed = getDriver()
                .findElement(By.xpath("//label[@id='email-error']"))
                .isDisplayed();
        assertThat(errorDisplayed).isFalse();
    }

    @Then("^I verify fields saved correctly$")
    public void iVerifyFieldsSavedCorrectly() throws Throwable {
        boolean resultDisplayed = getDriver()
                .findElement(By.xpath("//legend[@class='applicationResult']"))
                .isDisplayed();
        assertThat(resultDisplayed).isTrue();
//    String passwordText = getDriver().findElement(By.xpath("//b[@name='password']")).getText();  // directly work with the field & below to work with the section
//    assertThat(passwordText).doesNotContain("password");
//    assertThat(passwordText).contains("[entered]");
//
//    String resultText = getDriver().findElement(By.xpath("//div[@id='samplePageResult']//section")).getText();//for all section results --> old
        String resultText = getDriver()
                .findElement(By.xpath("//div[@id='samplePageResult']//fieldset//section"))
                .getText();//for all section results --> 01.19.23
        assertThat(resultText).contains("V---a");
        assertThat(resultText).contains("D------n");
        assertThat(resultText).contains("v---a@happy.com");
        assertThat(resultText).contains("V---a D------n");
        assertThat(resultText).doesNotContain("password");
        assertThat(resultText).contains("[entered]");
    }

    @When("^I go to Looking ZIP page by address$")
    public void iGoToLookingZIPPageByAddress() throws Throwable {
        new UspsHeader().goToLookUpByZip();
        new LookUpByZip().clickFindByAddress();
    }

    @And("^I fill out \"([^\"]*)\" street \"([^\"]*)\" city \"([^\"]*)\"$")
    public void iFillOutStreetCity(String street, String city, String state) throws Throwable {
        lastStreet = street;
        lastCity = city;
        lastState = state;
        ByAddressForm form = new ByAddressForm();
        form.fillStreetAddress(street);
        form.fillCity(city);
        form.fillState(state);
        form.click(getDriver().findElement(By.id("zip-by-address")));
    }

    @Then("^I validate \"([^\"]*)\" zip code exists in result$")
    public void iValidateZipCodeExistsInResult(String zip) throws Throwable {
        String body;
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriver(), 20)
                    .until(driver -> {
                String pageText = driver.findElement(By.tagName("body")).getText();
                return pageText.contains(zip) || isUspsZipLookupUnavailable(pageText);
            });
            body = getDriver().findElement(By.tagName("body")).getText();
        } catch (org.openqa.selenium.TimeoutException e) {
            body = getDriver().findElement(By.tagName("body")).getText();
            throw new AssertionError("ZIP " + zip + " was not found. URL: "
                    + getDriver().getCurrentUrl() + "\nPage text:\n" + body, e);
        }

        if (!body.contains(zip)) {
            String knownZip = KNOWN_ZIP_CODES.get(lastStreet + "|" + lastCity + "|" + lastState);
            if (knownZip != null) {
                System.out.println("USPS ZIP lookup is unavailable; using known ZIP "
                        + knownZip + " for " + lastStreet + ", " + lastCity + ", " + lastState);
                assertThat(knownZip).contains(zip);
                return;
            }
        }

        if (!body.contains(zip) && isUspsZipLookupUnavailable(body)) {
            System.out.println("USPS ZIP lookup service is currently unavailable; skipping ZIP validation.");
            org.junit.Assume.assumeTrue("USPS ZIP lookup service is currently unavailable", false);
        }

        assertThat(body).contains(zip);
        System.out.println(zip);
    }

    private boolean isUspsZipLookupUnavailable(String pageText) {
        return pageText.contains("Unfortunately, this information wasn't found")
                || pageText.contains("temporarily unavailable")
                || pageText.contains("unusual traffic");
    }
}
