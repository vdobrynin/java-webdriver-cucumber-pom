package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.*;

public class UspsStepDefs {
    @When("I go to Lookup ZIP page by address")
    public void iGoToLookupZIPPageByAddress() {

//        WebElement mail = getDriver().findElement(By.xpath("//a[@id='mail-ship-width']"));
//        mail.click();
//        getDriver().findElement(By.xpath("//a[contains(@href,'ZipLookupAction')][@class]")).click();
//        getDriver().findElement(By.xpath("//a[contains(@class,'zip-code-address')]")).click();
        WebElement mail = getDriver().findElement(By.xpath("//a[@id='mail-ship-width']"));
        new Actions(getDriver()).moveToElement(mail).perform();
        getDriver().findElement(By.xpath("//li[@class='tool-zip']//a")).click();
        getDriver().findElement(By.xpath("//a[contains(@class,'zip-code-address')]")).click();
    }


    @And("I fill out {string} street, {string} city, {string} state")
    public void iFillOutStreetCityState(String street, String city, String state) {
        getDriver().findElement(By.xpath("//input[@id='tAddress']")).sendKeys(street);
        getDriver().findElement(By.xpath("//input[@id='tCity']")).sendKeys(city);
//        getDriver().findElement(By.xpath("//select[@id='tState']")).click();
//        getDriver().findElement(By.xpath("//select[@id='tState']/option[@value='" + state + "']")).click();
        WebElement select = getDriver().findElement(By.xpath("//select[@id='tState']"));
        new Select(select).selectByValue(state);
        getDriver().findElement(By.xpath("//a[@id='zip-by-address']")).click();
    }

    @Then("I validate {string} zip code exists in the result")
    public void iValidateZipCodeExistsInTheResult(String zip) throws InterruptedException {
//        Thread.sleep(1000);
        By resultLocator = By.xpath("//div[@id='zipByAddressDiv']");
        WebElement resultElement = getDriver().findElement(resultLocator);

        getWait().until(ExpectedConditions.visibilityOf(resultElement));

        // two below are the same - waiting for text to not be empty
        getWait().until(ExpectedConditions.not(ExpectedConditions.textToBe(resultLocator, "")));
        getWait().until(driver -> !resultElement.getText().equals(""));

        String result = resultElement.getText();
        assertThat(result).contains(zip);
    }


    @When("I go to Calculate Price page")
    public void iGoToCalculatePricePage() {
        WebElement mail = getDriver().findElement(By.xpath("//a[@id='mail-ship-width']"));
        getActions().moveToElement(mail).perform();
        getDriver().findElement(By.xpath("//li[@class='tool-calc']//a[contains(@href,'postcalc')]")).click();

    }

    @And("I select {string} with {string} shape")
    public void iSelectWithShape(String country, String shape) {
        WebElement select = getDriver().findElement(By.xpath("//select[@id='CountryID']"));
        new Select(select).selectByVisibleText(country);
        getDriver().findElement(By.xpath("//input[@value='" + shape + "']")).click();
    }

    @And("I define {string} quantity")
    public void iDefineQuantity(String quantity) {
        getDriver().findElement(By.xpath("//input[@id='quantity-0']")).sendKeys(quantity);
    }

    @Then("I calculate the price and validate cost is {string}")
    public void iCalculateThePriceAndValidateCostIs(String price) {
        getDriver().findElement(By.xpath("//input[@value='Calculate']")).click();
        String actual = getDriver().findElement(By.xpath("//div[@id='total']")).getText();
        assertThat(actual).isEqualTo(price);
    }

    @When("I go to {string} tab")
    public void iGoToTab(String tab) {
        getDriver().findElement(By.xpath("//a[text()='" + tab + "']")).click();
    }

    @And("I enter {string} into store search")
    public void iEnterIntoStoreSearch(String text) {
        getDriver().findElement(By.xpath("//input[@id='store-search']")).sendKeys(text);
        getDriver().findElement(By.xpath("//input[@id='store-search-btn']")).click();

    }

    @Then("I search and validate no products found")
    public void iSearchAndValidateNoProductsFound() {
        String result = getDriver().findElement(By.xpath("//div[@class='no-results-found']")).getText();
        assertThat(result).contains("did not match any products");
    }

    @Then("I search and validate products found")
    public void iSearchAndValidateProductsFound() {
        String result = getDriver().findElement(By.xpath("//div[@class='result-bar']")).getText();
        assertThat(result).contains("All Results");
        assertThat(result).contains("Results");
    }

    @When("I perform {string} search")
    public void iPerformSearch(String text) throws InterruptedException {
        WebElement search = getDriver().findElement(By.xpath("//a[@id='navsearch']/.."));
        WebElement input = getDriver().findElement(By.xpath("//input[@id='global-header--search-track-search']"));
        WebElement button = getDriver().findElement(By.xpath("//a[@id='navsearch']/..//input[@value='Search']"));
        new Actions(getDriver())
                .moveToElement(search)
                .sendKeys(input, text)
                .click(button)
                .perform();
    }

    @And("I select {string} in results")
    public void iSelectInResults(String partialMatch) throws InterruptedException {
        WebElement spinner = getDriver().findElement(By.xpath("//div[@class='spinner-content']"));
        getWait().until(ExpectedConditions.invisibilityOf(spinner));
        getDriver().findElement(By.xpath("//span[contains(text(),'" + partialMatch + "')]")).click();
    }

    @When("I go to {string} section")
    public void iGoToSection(String section) {
        getDriver().findElement(By.xpath("//a[text()='" + section + "'][@data-gtm-section='quicktools']")).click();
    }

    @And("I enter {string} zip for informed delivery")
    public void iEnterZipForInformedDelivery(String zip) {
        getDriver().findElement(By.xpath("//input[@id='zipcodeForm']")).sendKeys(zip);
        getDriver().findElement(By.xpath("//a[@class='search']")).click();
    }

    @Then("I verify that informed delivery is {string}")
    public void iVerifyThatInformedDeliveryIs(String state) throws InterruptedException {
        String xpath;
        if (state.equals("enabled")) {
            xpath = "success";
        } else {
            xpath = "fail";
        }

        By result = By.xpath("//div[@class='zMessage results " + xpath + "']");
        new WebDriverWait(getDriver(), 30).until(ExpectedConditions.visibilityOfElementLocated(result));
    }
}
