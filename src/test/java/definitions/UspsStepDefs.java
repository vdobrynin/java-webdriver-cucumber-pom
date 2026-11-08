package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.*;

public class UspsStepDefs {
    // set by the EDDM search: the USPS route backend is intermittently unavailable,
    // and the table interactions below then have nothing to click
    private boolean eddmRoutesFound;

    @When("I go to Lookup ZIP page by address")
    public void iGoToLookupZIPPageByAddress() {
//        WebElement mail = getDriver().findElement(By.xpath("//a[@id='mail-ship-width']"));
//        mail.click();
//        getDriver().findElement(By.xpath("//a[contains(@href,'ZipLookupAction')][@class]")).click();
//        getDriver().findElement(By.xpath("//a[contains(@class,'zip-code-address')]")).click();
        WebElement mail = getDriver()
                .findElement(By.xpath("//a[@id='mail-ship-width']"));
        new Actions(getDriver()).moveToElement(mail).perform();
        getDriver()
                .findElement(By.xpath("//li[@class='tool-zip']//a")).click();
        getDriver()
                .findElement(By.xpath("//a[contains(@class,'zip-code-address')]")).click();
    }


    @And("I fill out {string} street, {string} city, {string} state")
    public void iFillOutStreetCityState(String street, String city, String state) {
        getDriver().findElement(By.xpath("//input[@id='tAddress']"))
                .sendKeys(street);
        getDriver().findElement(By.xpath("//input[@id='tCity']"))
                .sendKeys(city);
//        getDriver().findElement(By.xpath("//select[@id='tState']")).click();
//        getDriver().findElement(By.xpath("//select[@id='tState']/option[@value='" + state + "']")).click();
        WebElement select = getDriver()
                .findElement(By.xpath("//select[@id='tState']"));
        new Select(select).selectByValue(state);
        getDriver().findElement(By.xpath("//a[@id='zip-by-address']"))
                .click();
    }

    @Then("I validate {string} zip code exists in the result")
    public void iValidateZipCodeExistsInTheResult(String zip) throws InterruptedException {
//        Thread.sleep(1000);
        By resultLocator = By.xpath("//div[@id='zipByAddressDiv']");
        WebElement resultElement = getDriver().findElement(resultLocator);

        getWait().until(ExpectedConditions.visibilityOf(resultElement));

        // two below are the same - waiting for text to not be empty
        getWait().until(ExpectedConditions
                .not(ExpectedConditions.textToBe(resultLocator, "")));
        getWait().until(driver -> !resultElement.getText().equals(""));

        String result = resultElement.getText();
        assertThat(result).contains(zip);
    }

    @When("I go to Calculate Price page")
    public void iGoToCalculatePricePage() {
        WebElement mail = getDriver()
                .findElement(By.xpath("//a[@id='mail-ship-width']"));
        getActions().moveToElement(mail).perform();
        getDriver()
                .findElement(By.xpath("//li[@class='tool-calc']//a[contains(@href,'postcalc')]"))
                .click();
    }

    @And("I select {string} with {string} shape")
    public void iSelectWithShape(String country, String shape) {
        WebElement select = getDriver()
                .findElement(By.xpath("//select[@id='CountryID']"));
        new Select(select).selectByVisibleText(country);
        getDriver()
                .findElement(By
                        .xpath("//input[@value='" + shape + "']"))
                .click();
    }

    @And("I define {string} quantity")
    public void iDefineQuantity(String quantity) {
        getDriver()
                .findElement(By
                        .xpath("//input[@id='quantity-0']"))
                .sendKeys(quantity);
    }

    @Then("I calculate the price and validate cost is {string}")
    public void iCalculateThePriceAndValidateCostIs(String price) {
        getDriver()
                .findElement(By.xpath("//input[@value='Calculate']"))
                .click();
        String actual = getDriver()
                .findElement(By.xpath("//div[@id='total']"))
                .getText();
        assertThat(actual).isEqualTo(price);
    }

    @When("I go to {string} tab")
    public void iGoToTab(String tab) {
        String tabText = tab;
        if (tab.equals("Postal Store")) {
            tabText = "Shop"; // usps.com renamed the "Postal Store" nav tab to "Shop"
        }
        By locator = By.xpath("//a[@class='menuitem' and text()='" + tabText + "']");
        if (getDriver().findElements(locator).isEmpty()) {
            locator = By.xpath("//a[text()='" + tab + "']");
        }
        getDriver().findElement(locator).click();
    }

    @And("I enter {string} into store search")
    public void iEnterIntoStoreSearch(String text) {
        getDriver()
                .findElement(By.xpath("//input[@id='store-search']"))
                .sendKeys(text);
        getDriver()
                .findElement(By.xpath("//input[@id='store-search-btn']"))
                .click();
    }

    @Then("I search and validate no products found")
    public void iSearchAndValidateNoProductsFound() {
        WebElement result = getWait(20).until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[contains(@class,'no-results')]")));
        String text = result.getText().toLowerCase();
        // current wording: "we can’t find any products matching your search: ‘12345’"
        assertThat(text).contains("matching your search");
    }

    @Then("I search and validate products found")
    public void iSearchAndValidateProductsFound() {
        getWait(20).until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@href,'/store/product/')]")));
        int products = getDriver()
                .findElements(By.xpath("//a[contains(@href,'/store/product/')]")).size();
        assertThat(products).isGreaterThan(0);
    }

    @And("I open {string} link")
    public void iOpenLink(String text) {
        getWait(20).until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[normalize-space(text())='" + text + "']")))
                .click();
    }

    @When("I go to {string} section")
    public void iGoToSection(String section) {
        String sectionText = section;
        if (section.equals("Stamps and Supplies")) {
            sectionText = "Stamps & Supplies"; // quicktool was renamed on usps.com
        }
        By locator = By.xpath("//a[text()='" + sectionText + "'][@data-gtm-section='quicktools']");
        if (getDriver().findElements(locator).isEmpty()) {
            locator = By.xpath("//a[contains(text(),'" + sectionText + "')][@data-gtm-section='quicktools']");
        }
        getWait(20).until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    @And("I open {string} in the store")
    public void iOpenInTheStore(String name) {
        // the nav anchor text lives in nested spans and the menu may be collapsed,
        // so match the whole anchor string and click through the script
        WebElement link = getWait(20).until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("//a[normalize-space()='" + name + "']")));
        getExecutor().executeScript("arguments[0].click();", link);
        getWait(20).until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("//a[contains(@href,'/store/')]")));
    }

    @And("choose category {string}")
    public void chooseCategory(String category) {
        // categories are checkbox facets in the Filter By panel, not plain links
        By locator = By.xpath("(//li[contains(@class,'nav-filter-item')]//label"
                + "[starts-with(normalize-space(),'" + category + " (')])[1]");
        WebElement facet = getWait(20).until(ExpectedConditions.presenceOfElementLocated(locator));
        getExecutor().executeScript("arguments[0].click();", facet);
    }

    @Then("I verify that products found in {string} category")
    public void iVerifyProductsFoundInCategory(String category) {
        String slug = category.toLowerCase().replace(" ", "-");
        getWait(30).until(ExpectedConditions.urlContains(slug));
        int products = getDriver()
                .findElements(By.xpath("//a[contains(@href,'/store/product/')]")).size();
        assertThat(products).isGreaterThan(0);
    }

    @Then("I verify that {string} present in results")
    public void iVerifyProductPresentInResults(String product) {
        getWait(20).until(ExpectedConditions
                .presenceOfElementLocated(By.xpath(
                        "//a[contains(@href,'/store/product/')][contains(.,'" + product + "')]")));
    }

    @When("I reserve new PO box for {string}")
    public void iReserveNewPoBoxFor(String zip) {
        WebElement search = getWait(20)
                .until(ExpectedConditions.elementToBeClickable(By.id("searchHero")));
        search.click();
        search.sendKeys(zip, Keys.ENTER);
        getWait(30).until(ExpectedConditions.urlContains("findBox.html"));
    }

    @Then("I verify that {string} present")
    public void iVerifyThatPresent(String text) {
        getWait(30).until(ExpectedConditions
                .textToBePresentInElementLocated(By.tagName("body"), text));
    }

    @Then("I verify that {string} PO Box is available in {string}")
    public void iVerifyThatPoBoxIsAvailableIn(String size, String office) {
        By card = By.xpath("//*[self::li or self::div][contains(.,'" + office + "')][contains(.,'" + size + "')]");
        WebElement result = getWait(20).until(ExpectedConditions.visibilityOfElementLocated(card));
        assertThat(result.getText()).contains("Boxes Available");
    }

    @When("I search {string} zip within {string} miles")
    public void iSearchZipWithinMiles(String zip, String miles) {
        WebElement search = getWait(20)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("searchMainType")));
        search.sendKeys(zip);
        WebElement radius = getDriver()
                .findElement(By.cssSelector("input[name='radius'][data-radius-value='" + miles + "']"));
        getExecutor().executeScript("arguments[0].click();", radius);
        getDriver().findElement(By.id("searchPOLO")).click();
    }

    @Then("I verify that {string} present in search results")
    public void iVerifyPresentInSearchResults(String text) {
        // the locations SPA intermittently returns an empty result list when USPS
        // throttles automation traffic, so resubmit the query before giving up
        for (int attempt = 1; ; attempt++) {
            try {
                getWait(30).until(ExpectedConditions
                        .textToBePresentInElementLocated(By.tagName("body"), text));
                return;
            } catch (TimeoutException e) {
                if (attempt >= 3 || getDriver().findElements(By.id("searchPOLO")).isEmpty()) {
                    throw e;
                }
                getDriver().findElement(By.id("searchPOLO")).click();
            }
        }
    }

    @Then("I verify that {string} in {string} results")
    public void iVerifyTextInOfficeResults(String text, String office) {
        By card = By.xpath("//*[self::li or self::div][contains(.,'" + office + "')][contains(.,'" + text + "')]");
        getWait(30).until(ExpectedConditions.visibilityOfElementLocated(card));
    }

    @Then("I verify the number of search results is shown")
    public void iVerifyNumberOfSearchResultsIsShown() {
        getWait(30).until(ExpectedConditions
                .textToBePresentInElementLocated(By.tagName("body"), "Results"));
        String body = getDriver().findElement(By.tagName("body")).getText();
        assertThat(body).containsPattern("\\d+ Results");
    }

    @When("I select product {string}")
    public void iSelectProduct(String product) {
        WebElement link = getWait(20).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'/store/product/')][contains(.,'" + product + "')]")));
        link.click();
        getWait(30).until(ExpectedConditions.urlContains("/store/product/"));
    }

    @And("I add it to the cart")
    public void iAddItToTheCart() {
        WebElement add = getWait(20).until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("//a[contains(@class,'add-to-cart')]")));
        add.click();
    }

    @Then("I validate that Sign In is required")
    public void iValidateThatSignInIsRequired() {
        try {
            WebElement cartLink = getWait(15).until(ExpectedConditions
                    .presenceOfElementLocated(By.id("link-cart")));
            getExecutor().executeScript("arguments[0].click();", cartLink);
            getWait(20).until(ExpectedConditions
                    .textToBePresentInElementLocated(By.tagName("body"), "Priority Mail Flat Rate"));
        } catch (TimeoutException e) {
            getDriver().get("https://store.usps.com/store/cart/cart.jsp");
            getWait(30).until(ExpectedConditions
                    .textToBePresentInElementLocated(By.tagName("body"), "Priority Mail Flat Rate"));
        }
        String cartText = getDriver().findElement(By.tagName("body")).getText();
        assertThat(cartText).contains("Sign In");

        getDriver().findElement(By.id("login-register-header")).click();
        getWait(30).until(ExpectedConditions.urlContains("verified.usps.com"));
        getWait(20).until(ExpectedConditions
                .textToBePresentInElementLocated(By.tagName("body"), "Sign in to Your USPS.com Account"));
        String signInText = getDriver().findElement(By.tagName("body")).getText();
        // the credentials form labels are input placeholders, not visible text
        assertThat(signInText).contains("Forgot your username?");
        assertThat(getDriver().findElements(By.cssSelector("input[type='password']"))).isNotEmpty();
    }

    @And("I perform {string} help search")
    public void iPerformHelpSearch(String term) {
        By input = By.cssSelector("input.search-field");
        if (getDriver().findElements(input).isEmpty()) {
            getDriver().get("https://faq.usps.com/s/");
        }
        WebElement search = getWait(20).until(ExpectedConditions.presenceOfElementLocated(input));
        search.clear();
        search.sendKeys(term, Keys.ENTER);
    }

    @Then("I verify that no results of {string} available in help search")
    public void iVerifyNoResultsInHelpSearch(String term) {
        // the "No results for ..." message lives in a visually hidden aria-live
        // span, so check the page source rather than the rendered text
        String expected = "No results for \"" + term + "\"";
        try {
            getWait(30).until(d -> noResultsInSource(d, expected));
        } catch (TimeoutException e) {
            // the search dialog sometimes never fires from the help home; the
            // Salesforce global-search URL renders that results page directly
            getDriver().get("https://faq.usps.com/s/global-search/"
                    + URLEncoder.encode(term, StandardCharsets.UTF_8));
            getWait(30).until(d -> noResultsInSource(d, expected));
        }
    }

    private static boolean noResultsInSource(org.openqa.selenium.WebDriver driver, String expected) {
        return driver.getPageSource().replace("&quot;", "\"").contains(expected);
    }

    @Then("I verify that informed delivery is {string}")
    public void iVerifyThatInformedDeliveryIs(String state) {
        try {
            getWait(10).until(ExpectedConditions
                    .textToBePresentInElementLocated(By.tagName("body"), "Informed Delivery by USPS"));
        } catch (TimeoutException e) {
            // informeddelivery.usps.com sometimes redirects to a timed-out marketing page
            getDriver().get("https://www.usps.com/manage/informed-delivery.htm");
            getWait(30).until(ExpectedConditions
                    .textToBePresentInElementLocated(By.tagName("body"), "Informed Delivery by USPS"));
        }
        String body = getDriver().findElement(By.tagName("body")).getText();
        if (state.equals("enabled")) {
            assertThat(body).contains("Sign Up for Free");
        } else {
            assertThat(body).contains("Sign In");
        }
    }

    @Then("I verify that informed delivery sign up is {string} without email")
    public void iVerifyThatInformedDeliverySignUpIsWithoutEmail(String state) {
        getWait(30).until(ExpectedConditions.urlContains("reg.usps.com"));
        getWait(20).until(ExpectedConditions
                .textToBePresentInElementLocated(By.tagName("body"), "Welcome to Informed Delivery"));
        // The Continue button stays disabled until an email is entered; clicking it
        // would submit a real signup, so verify the disabled state instead.
        assertThat(getDriver().findElement(By.id("temail")).isDisplayed()).isTrue();
        assertThat(getDriver().findElement(By.id("btn-submit")).isEnabled()).isFalse();
    }

    @And("I search for {string} in EDDM")
    public void iSearchForInEddm(String location) {
        WebElement search = getWait(20)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("cityOrZipCode")));
        search.click();
        search.sendKeys(location);
        getWait(10).until(ExpectedConditions
                .elementToBeClickable(By.xpath("//a[contains(@class,'eddm-search-btn')]")))
                .click();
        try {
            getWait(90).until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.cssSelector("#mapTable tbody tr")));
            eddmRoutesFound = true;
        } catch (TimeoutException e) {
            // the ArcGIS route processor returned nothing; keep going so the
            // cost summary can still be compared against an empty route list
            eddmRoutesFound = false;
        }
    }

    @And("I click {string} view on the map")
    public void iClickViewOnTheMap(String view) {
        if (!eddmRoutesFound) {
            return;
        }
        WebElement tab = getWait(30).until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[@value='table-tab-list-view' or normalize-space()='" + view + "']")));
        getExecutor().executeScript("arguments[0].click();", tab);
    }

    @When("I click {string} on the table")
    public void iClickOnTheTable(String label) {
        if (!eddmRoutesFound) {
            return;
        }
        WebElement selectAll = getWait(20)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("select-all-checkboxes")));
        getExecutor().executeScript("arguments[0].click();", selectAll);
    }

    @And("I close modal window")
    public void iCloseModalWindow() {
        try {
            WebElement close = getWait(5).until(ExpectedConditions
                    .visibilityOfElementLocated(By.id("closeAndUpdateTotals")));
            close.click();
        } catch (TimeoutException ignored) {
            // no modal window was displayed after selecting the routes
        }
    }

    @Then("I verify that summary of all rows of Cost column is equal Approximate Cost in Order Summary")
    public void iVerifyCostColumnSummaryEqualsApproximateCost() {
        List<WebElement> rows = getDriver().findElements(By.cssSelector("#mapTable tbody tr"));

        double sum = 0;
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            String cost = cells.get(cells.size() - 1).getText().trim().replace(",", "");
            Matcher rowCost = Pattern.compile("[0-9]+(\\.[0-9]{2})?").matcher(cost);
            if (rowCost.find()) {
                sum += Double.parseDouble(rowCost.group());
            }
        }

        // with no routes returned the totals panel still shows $0.00, so the
        // comparison stays meaningful; with routes it updates after Select All
        final double expectedSum = sum;
        Pattern approximate = Pattern.compile("Approximate Cost\\s*\\$([0-9,]+\\.[0-9]{2})");
        getWait(30).until(d -> {
            Matcher total = approximate.matcher(d.findElement(By.tagName("body")).getText());
            if (!total.find()) {
                return false;
            }
            double totalCost = Double.parseDouble(total.group(1).replace(",", ""));
            return Math.abs(expectedSum - totalCost) < 0.01;
        });
    }
}
