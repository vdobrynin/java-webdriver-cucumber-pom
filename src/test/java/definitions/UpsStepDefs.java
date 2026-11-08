package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.*;

public class UpsStepDefs {
    private static final String GUIDED_ORIGIN_URL = "https://www.ups.com/ship/guided/origin?loc=en_US&src=FWS";
    private String lastTotalCharges;

    @And("I open Shipping menu")
    public void iOpenShippingMenu() {
        getWait(20).until(driver -> {
            for (WebElement menu : driver.findElements(
                    By.xpath("//button[@aria-controls='subsection-shipping']"))) {
                if (menu.isDisplayed()) {
                    return true;
                }
            }
            for (WebElement menu : driver.findElements(
                    By.xpath("//div[contains(@class,'nav-hamburger')]/button"))) {
                if (menu.isDisplayed()) {
                    return true;
                }
            }
            return false;
        });

        List<WebElement> desktopMenu = getDriver()
                .findElements(By.xpath("//button[@aria-controls='subsection-shipping']"));
        for (WebElement menu : desktopMenu) {
            if (menu.isDisplayed()) {
                jsClick(menu);
                return;
            }
        }

        List<WebElement> hamburger = getDriver()
                .findElements(By.xpath("//div[contains(@class,'nav-hamburger')]/button"));
        for (WebElement button : hamburger) {
            if (button.isDisplayed()) {
                jsClick(button);
                break;
            }
        }

        List<WebElement> mobileMenu = getDriver()
                .findElements(By.xpath("//button[@aria-controls='mobile-subsection-shipping']"));
        for (WebElement menu : mobileMenu) {
            if (menu.isDisplayed()) {
                jsClick(menu);
                return;
            }
        }

        getDriver().findElement(By.id("ups-menuLinks1")).click();
    }

    @And("I go to Create a Shipment")
    public void iGoToCreateAShipment() {
        getWait(10).until(driver -> !driver
                .findElements(By.xpath("//a[.//span[normalize-space()='Create a Shipment']]")).isEmpty());

        List<WebElement> createShipmentLinks = getDriver()
                .findElements(By.xpath("//a[.//span[normalize-space()='Create a Shipment']]"));
        boolean clicked = false;
        for (WebElement link : createShipmentLinks) {
            if (link.isDisplayed()) {
                link.click();
                clicked = true;
                break;
            }
        }
        if (!clicked) {
            if (!createShipmentLinks.isEmpty()) {
                jsClick(createShipmentLinks.get(0));
            } else {
                getDriver()
                        .findElement(By.xpath("//ul[@class='ups-menu_links']//a[contains(@href,'ship?')]"))
                        .click();
            }
        }

        // Use the guided workflow; the default single-page workflow does not expose
        // the same navigation buttons used by these scenarios.
        getDriver().get(GUIDED_ORIGIN_URL);
        getWait(30).until(ExpectedConditions
                .visibilityOfElementLocated(By.id("origin-cac_companyOrName")));
    }

    @When("I fill out origin shipment fields")
    public void iFillOutOriginShipmentFields() throws InterruptedException {
        Map<String, String> sender = getData("sender");
        dismissCookieBanner();

        typeById("origin-cac_companyOrName", sender.get("name"));
        typeById("origin-cac_contactName", sender.get("name"));
        typeById("origin-cac_singleLineAddress", sender.get("address"));
        Thread.sleep(2000);
        getDriver().findElement(By.id("origin-cac_singleLineAddress")).sendKeys(Keys.ESCAPE);
        typeById("origin-cac_email", sender.get("email"));
        typeById("origin-cac_phone", sender.get("phone"));
    }

    @And("I submit the shipment form")
    public void iSubmitTheShipmentForm() {
        String url = getDriver().getCurrentUrl();

        if (url.contains("/guided/origin")) {
            clickById("nbsBackForwardNavigationContinueButton");
            getWait(20).until(ExpectedConditions
                    .visibilityOfElementLocated(By.id("origin-cac_postalCode")));
            fillDetailedAddress("origin", getData("sender"));
            clickById("nbsBackForwardNavigationContinueButton");
            handleAddressClassification("/guided/destination");
        } else if (url.contains("/guided/destination")) {
            clickById("nbsBackForwardNavigationContinueButton");
            getWait(20).until(ExpectedConditions
                    .visibilityOfElementLocated(By.id("destination-cac_postalCode")));
            fillDetailedAddress("destination", getData("receiver"));
            clickById("nbsBackForwardNavigationContinueButton");
            handleAddressClassification("/guided/package");
        } else if (url.contains("/guided/package")) {
            clickById("nbsBackForwardNavigationContinueButton");
            getWait(25).until(ExpectedConditions.urlContains("/guided/pickup-service"));
        } else if (url.contains("/guided/pickup-service")) {
            clickById("nbsBackForwardNavigationContinueButton");
            getWait(25).until(ExpectedConditions.urlContains("/guided/options"));
        } else if (url.contains("/guided/options")) {
            submitDetailsAndWaitForPayment();
        } else if (url.contains("/guided/payment")) {
            clickById("nbsBackForwardNavigationReviewPrimaryButton");
            getWait(30).until(ExpectedConditions.urlContains("/ship/single-page"));
        } else {
            clickById("nbsBackForwardNavigationContinueButton");
        }
    }

    private void submitDetailsAndWaitForPayment() {
        By description = By.id("nbsShipmentDescription");
        for (int attempt = 1; attempt <= 3; attempt++) {
            if (getDriver().getCurrentUrl().contains("/guided/payment")) {
                return;
            }
            // Selecting Saturday Delivery re-rates the shipment and re-renders
            // this form. Make sure the required description is still filled
            // before asking UPS to continue.
            getWait(10).until(ExpectedConditions.presenceOfElementLocated(description));
            WebElement descriptionField = getDriver().findElement(description);
            if (descriptionField.getAttribute("value").trim().isEmpty()) {
                typeById("nbsShipmentDescription", "Test");
            }

            waitFor(1000);
            clickById("nbsBackForwardNavigationContinueButton");
            try {
                getWait(12).until(ExpectedConditions.urlContains("/guided/payment"));
                return;
            } catch (TimeoutException e) {
                System.out.println("Details page did not advance to payment (attempt "
                        + attempt + "), URL: " + getDriver().getCurrentUrl());
                if (attempt == 3) {
                    printVisibleValidationErrors();
                    throw e;
                }
                waitFor(1000);
            }
        }
    }

    private void printVisibleValidationErrors() {
        List<WebElement> errors = getDriver().findElements(By.xpath(
                "//*[contains(@class,'error') or contains(@class,'invalid')]"));
        for (WebElement error : errors) {
            if (error.isDisplayed() && !error.getText().trim().isEmpty()) {
                System.out.println("Validation error: " + error.getText().trim());
            }
        }
    }

    @Then("I verify origin shipment fields submitted")
    public void iVerifyOriginShipmentFieldsSubmitted() {
        Map<String, String> sender = getData("sender");
        getWait(20).until(ExpectedConditions
                .visibilityOfElementLocated(By.id("destination-cac_companyOrName")));

        String pageText = getDriver().findElement(By.tagName("body")).getText();
        assertThat(pageText).containsIgnoringCase(sender.get("name"));
        assertThat(pageText).containsIgnoringCase(sender.get("address"));
        assertThat(pageText).containsIgnoringCase(sender.get("zip"));
        assertThat(pageText).containsIgnoringCase(sender.get("city"));
        assertThat(pageText).containsIgnoringCase(sender.get("state"));
        assertThat(pageText).containsIgnoringCase(sender.get("phone"));
        assertThat(pageText).containsIgnoringCase(sender.get("email"));
    }

    @And("I cancel the shipment form")
    public void iCancelTheShipmentForm() {
        clickById("nbsBackForwardNavigationCancelShipmentButton");
        getWait(10).until(ExpectedConditions
                .visibilityOfElementLocated(By.id("nbsCancelShipmentWarningYes")));
        jsClick(getDriver().findElement(By.id("nbsCancelShipmentWarningYes")));
        getWait(30).until(ExpectedConditions.urlContains("/guided/origin"));
    }

    @Then("I verify shipment form is reset")
    public void iVerifyShipmentFormIsReset() {
        getWait(20).until(ExpectedConditions
                .visibilityOfElementLocated(By.id("origin-cac_companyOrName")));
        String nameValue = getDriver()
                .findElement(By.id("origin-cac_companyOrName"))
                .getAttribute("value");
        assertThat(nameValue).isEmpty();
    }

    @When("I fill out destination shipment fields")
    public void iFillOutDestinationShipmentFields() throws InterruptedException {
        Map<String, String> receiver = getData("receiver");
        typeById("destination-cac_companyOrName", receiver.get("name"));
        typeById("destination-cac_singleLineAddress", receiver.get("address"));
        Thread.sleep(2000);
        getDriver().findElement(By.id("destination-cac_singleLineAddress")).sendKeys(Keys.ESCAPE);
    }

    @And("I set packaging type")
    public void iSetPackagingType() throws InterruptedException {
        getWait(20).until(ExpectedConditions
                .visibilityOfElementLocated(By.id("nbsPackagePackagingTypeDropdown0")));
        Thread.sleep(2000);

        WebElement packagingType = getDriver()
                .findElement(By.id("nbsPackagePackagingTypeDropdown0"));
        Select select = new Select(packagingType);
        try {
            select.selectByVisibleText("My Packaging");
        } catch (NoSuchElementException e) {
            select.selectByValue("29: Object");
        }
        Thread.sleep(1000);

        typeById("nbsPackagePackageWeightField0", "1");
        typeById("nbsPackagePackageLengthField0", "10");
        typeById("nbsPackagePackageWidthField0", "10");
        typeById("nbsPackagePackageHeightField0", "10");
        Thread.sleep(1000);
    }

    @Then("I verify total charges changed")
    public void iVerifyTotalChargesChanged() {
        getWait(25).until(driver -> getTotalChargesText().contains("$"));

        String currentTotal = getTotalChargesText().trim();
        assertThat(currentTotal)
                .as("Total charges should be displayed")
                .contains("$");

        if (lastTotalCharges != null) {
            assertThat(currentTotal)
                    .as("Total charges should change after updating the shipment")
                    .isNotEqualTo(lastTotalCharges);
        }
        lastTotalCharges = currentTotal;
    }

    @And("I select a Saturday-eligible delivery option")
    public void iSelectSaturdayEligibleDeliveryOption() {
        // Saturday Delivery is only offered for a service/ship-date combination
        // where a Saturday arrival makes sense (for example Next Day Air shipped
        // on a Friday). Pin the optional ship date to the next Friday so the
        // scenario is independent of the day it runs on.
        setEstimatedShipDateNextFriday();
        selectService("UPS Next Day Air");
    }

    private void setEstimatedShipDateNextFriday() {
        LocalDate nextFriday = LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        String date = nextFriday.format(DateTimeFormatter.ofPattern("M/d/yyyy"));

        WebElement dateField = getDriver().findElement(By.id("nbsDropOffDatePicker"));
        jsClick(dateField);
        getExecutor().executeScript(
                "var el = arguments[0];"
                        + "var setter = Object.getOwnPropertyDescriptor("
                        + "  window.HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(el, arguments[1]);"
                        + "el.dispatchEvent(new Event('input', {bubbles: true}));"
                        + "el.dispatchEvent(new Event('change', {bubbles: true}));"
                        + "el.dispatchEvent(new Event('blur', {bubbles: true}));",
                dateField, date);
        if (!date.equals(dateField.getAttribute("value"))) {
            boolean mac = System.getProperty("os.name", "").contains("Mac");
            dateField.sendKeys(mac ? Keys.chord(Keys.COMMAND, "a") : Keys.chord(Keys.CONTROL, "a"));
            dateField.sendKeys(Keys.DELETE);
            dateField.sendKeys(date);
            dateField.sendKeys(Keys.ESCAPE);
        }

        getWait(20).until(driver -> date.equals(
                driver.findElement(By.id("nbsDropOffDatePicker")).getAttribute("value")));
        // Give UPS a moment to re-rate the shipment for the new ship date.
        waitFor(4000);
    }

    private void selectService(String serviceName) {
        By serviceLabel = By.xpath("//label[.//p[contains(@id,'nbsServiceTileServiceDescription')"
                + " and normalize-space()='" + serviceName + "']]");
        getWait(25).until(ExpectedConditions.presenceOfElementLocated(serviceLabel));
        waitFor(3000);

        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                for (WebElement label : getDriver().findElements(serviceLabel)) {
                    if (label.isDisplayed()) {
                        jsClick(label);
                        return;
                    }
                }
            } catch (StaleElementReferenceException ignored) {
                // The rate list is still refreshing; retry with fresh elements.
            }
            waitFor(1000);
        }
        throw new NoSuchElementException("Service tile not found: " + serviceName);
    }

    private void waitFor(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @And("I set Saturday Delivery type")
    public void iSetSaturdayDeliveryType() {
        getWait(20).until(ExpectedConditions
                .visibilityOfElementLocated(By.id("nbsShipmentDescription")));
        typeById("nbsShipmentDescription", "Test");

        WebElement saturdayDelivery = getWait(20)
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.id("nbsSaturdayDeliveryOptionBaseOptionSwitch")));
        if (!saturdayDelivery.isSelected()) {
            jsClick(saturdayDelivery);
        }
    }

    @And("I select Paypal payment type")
    public void iSelectPaypalPaymentType() throws InterruptedException {
        By paypalLabel = By.xpath("//label[@for='other-ways-to-pay-tile']");
        getWait(20).until(ExpectedConditions.visibilityOfElementLocated(paypalLabel));
        jsClick(getDriver().findElement(paypalLabel));
        Thread.sleep(1000);
    }

    @Then("I review all recorded details on the review page")
    public void iReviewAllRecordedDetailsOnTheReviewPage() {
        Map<String, String> sender = getData("sender");
        Map<String, String> receiver = getData("receiver");

        getWait(30).until(ExpectedConditions.visibilityOfElementLocated(
                By.id("nbsBackForwardNavigationPayAndGetLabelsButton")));

        String pageText = getDriver().findElement(By.tagName("body")).getText();
        assertThat(pageText).containsIgnoringCase(sender.get("name"));
        assertThat(pageText).containsIgnoringCase(sender.get("address"));
        assertThat(pageText).containsIgnoringCase(sender.get("city"));
        assertThat(pageText).containsIgnoringCase(sender.get("zip"));
        assertThat(pageText).containsIgnoringCase(receiver.get("name"));
        assertThat(pageText).containsIgnoringCase(receiver.get("address"));
        assertThat(pageText).containsIgnoringCase(receiver.get("city"));
        assertThat(pageText).containsIgnoringCase(receiver.get("zip"));
        assertThat(pageText).containsIgnoringCase("Saturday");
        assertThat(pageText).containsIgnoringCase("PayPal");
    }

    private void fillDetailedAddress(String prefix, Map<String, String> address) {
        WebElement addressLine1 = getDriver().findElement(By.id(prefix + "-cac_addressLine1"));
        if (addressLine1.getAttribute("value").isEmpty()) {
            typeById(prefix + "-cac_addressLine1", address.get("address"));
        }
        typeById(prefix + "-cac_postalCode", address.get("zip"));
        typeById(prefix + "-cac_city", address.get("city"));
        new Select(getDriver().findElement(By.id(prefix + "-cac_state")))
                .selectByValue(address.get("state"));
    }

    private void handleAddressClassification(String expectedUrlPart) {
        getWait(20).until(driver -> {
            List<WebElement> modal = driver.findElements(By.id("nbsAddressClassificationContinue"));
            return (modal.size() > 0 && modal.get(0).isDisplayed())
                    || driver.getCurrentUrl().contains(expectedUrlPart);
        });

        List<WebElement> modal = getDriver().findElements(By.id("nbsAddressClassificationContinue"));
        if (!modal.isEmpty() && modal.get(0).isDisplayed()) {
            jsClick(modal.get(0));
        }

        getWait(30).until(ExpectedConditions.urlContains(expectedUrlPart));
    }

    private void dismissCookieBanner() {
        List<WebElement> closeButtons = getDriver().findElements(
                By.cssSelector("#onetrust-close-btn-container button, .onetrust-close-btn-handler"));
        for (WebElement button : closeButtons) {
            if (button.isDisplayed()) {
                jsClick(button);
                return;
            }
        }
    }

    private void typeById(String id, String value) {
        getWait(20).until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        WebElement element = getDriver().findElement(By.id(id));
        try {
            element.click();
        } catch (WebDriverException e) {
            jsClick(element);
        }
        element.clear();
        element.sendKeys(value);

        if (!value.equals(element.getAttribute("value"))) {
            getExecutor().executeScript(
                    "arguments[0].value = arguments[1];"
                            + "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));"
                            + "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));"
                            + "arguments[0].dispatchEvent(new Event('blur', {bubbles:true}));",
                    element, value);
        }
    }

    private void clickById(String id) {
        getWait(20).until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        jsClick(getDriver().findElement(By.id(id)));
    }

    private void jsClick(WebElement element) {
        getExecutor().executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        getExecutor().executeScript("arguments[0].click();", element);
    }

    private String getTotalChargesText() {
        List<WebElement> containers = getDriver().findElements(By.id("nbsBalanceBarContainer"));
        for (WebElement container : containers) {
            if (container.isDisplayed() && !container.getText().trim().isEmpty()) {
                return container.getText();
            }
        }

        String bodyText = getDriver().findElement(By.tagName("body")).getText();
        int index = bodyText.indexOf("Total Charges");
        if (index >= 0) {
            return bodyText.substring(index, Math.min(bodyText.length(), index + 100));
        }
        return bodyText;
    }

}
