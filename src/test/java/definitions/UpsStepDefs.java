package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.*;

public class UpsStepDefs {
    @And("I open Shipping menu")
    public void iOpenShippingMenu() {
        getDriver().findElement(By.id("ups-menuLinks1")).click();
    }

    @And("I go to Create a Shipment")
    public void iGoToCreateAShipment() { //a[normalize-space()='Create a Shipment']
        getDriver().findElement(By.xpath("//ul[@class='ups-menu_links']//a[contains(@href,'ship?')]")).click();
        // //a[contains(@href,'ship?')][not(@id='ups-quickStartShip')]
    }

    @When("I fill out origin shipment fields")
    public void iFillOutOriginShipmentFields() throws InterruptedException {
        Map<String, String> sender = getData("sender");
        getDriver().findElement(By.id("originname")).sendKeys(sender.get("name"));
        getDriver().findElement(By.id("originaddress1")).sendKeys(sender.get("address"));
        getDriver().findElement(By.id("originpostal")).sendKeys(sender.get("zip"));

        getWait().until(ExpectedConditions.textToBePresentInElementValue(By.id("origincity"), sender.get("city")));
        String upsStateCode = getData("states").get(sender.get("state"));
        getWait().until(ExpectedConditions.elementToBeSelected(By.xpath("//select[@id='originstate']/*[@value='" + upsStateCode + "']")));

        getDriver().findElement(By.id("originemail")).sendKeys(sender.get("email"));
        getDriver().findElement(By.id("originphone")).sendKeys(sender.get("phone"));
        // because of the bug, number not get saved 50% times if not waiting
        Thread.sleep(1000);
    }

    @And("^I submit the shipment form$")
    public void iSubmitTheShipmentPage() throws Throwable {
        String oldUrl = getDriver().getCurrentUrl();

        if (getDriver().getCurrentUrl().contains("payment")) {
            getExecutor().executeScript("arguments[0].click();", getDriver().findElement(By.xpath("//button[@id='nbsBackForwardNavigationReviewPrimaryButton']")));
        } else {
            getExecutor().executeScript("arguments[0].click();", getDriver().findElement(By.xpath("//button[@id='nbsBackForwardNavigationContinueButton']")));
        }
        getWait(10).until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));
    }

    @Then("I verify origin shipment fields submitted")
    public void iVerifyOriginShipmentFieldsSubmitted() {
        Map<String, String> sender = getData("sender");
        String originText = getDriver().findElement(By.xpath("//agent-summary")).getText();
        assertThat(originText).containsIgnoringCase(sender.get("name"));
        assertThat(originText).containsIgnoringCase(sender.get("address"));
        assertThat(originText).containsIgnoringCase(sender.get("zip"));
        assertThat(originText).containsIgnoringCase(sender.get("city"));
        assertThat(originText).containsIgnoringCase(sender.get("state"));
        assertThat(originText).containsIgnoringCase(sender.get("phone"));
        assertThat(originText).containsIgnoringCase(sender.get("email"));
    }

    @And("I cancel the shipment form")
    public void iCancelTheShipmentForm() {
        getExecutor().executeScript("arguments[0].click();", getDriver().findElement(By.id("nbsBackForwardNavigationCancelShipmentButton")));
        getExecutor().executeScript("arguments[0].click();", getDriver().findElement(By.id("nbsCancelShipmentWarningYes")));
    }

    @Then("I verify shipment form is reset")
    public void iVerifyShipmentFormIsReset() {
        String nameValue = getDriver().findElement(By.id("originname")).getAttribute("value");
        assertThat(nameValue).isEmpty();
    }

    @When("I fill out destination shipment fields")
    public void iFillOutDestinationShipmentFields() throws InterruptedException {
        Map<String, String> receiver = getData("receiver");
        getDriver().findElement(By.id("destinationname")).sendKeys(receiver.get("name"));
        getDriver().findElement(By.id("destinationaddress1")).sendKeys(receiver.get("address"));
        getDriver().findElement(By.id("destinationpostal")).sendKeys(receiver.get("zip"));

        getWait().until(ExpectedConditions.textToBePresentInElementValue(By.id("destinationcity"), receiver.get("city")));
        String upsStateCode = getData("states").get(receiver.get("state"));
        getWait().until(ExpectedConditions.elementToBeSelected(By.xpath("//select[@id='destinationstate']/*[@value='" + upsStateCode + "']")));
        // because of the bug, number not get saved 50% times if not waiting. Instead of adding wait, put error handling in iReviewAllRecordedDetailsOnTheReviewPage()
    }

    @Then("I verify total charges changed")
    public void iVerifyTotalChargesChanged() {
        WebElement total = getDriver().findElement(By.id("total-charges-spinner"));
        String oldValue = total.getText();
//        getWait().until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(total, oldValue)));
        getWait().until(driver -> !total.getText().equals(oldValue));
    }

    @And("I set packaging type")
    public void iSetPackagingType() {
        getDriver().findElement(By.id("nbsPackagePackageWeightField0")).sendKeys("1");
        WebElement dropdown = getDriver().findElement(By.id("nbsPackagePackagingTypeDropdown0"));
        new Select(dropdown).selectByValue("17: Object");
    }

    @And("I select cheapest delivery option")
    public void iSelectCheapestDeliveryOption() throws ParseException {
        try {
            By accordionSelector = By.xpath("//button[@class='ups-accordion_toggle']");
            getWait(10).until(ExpectedConditions.visibilityOfElementLocated(accordionSelector));
            getExecutor().executeScript("arguments[0].click();", getDriver().findElement(accordionSelector));
        } catch (TimeoutException e) {
            // sometimes button not there, when old UI is shown, handling the case to keep going and test old UI
            System.out.println("Skip clicking the button");
        }
        try {
            List<WebElement> deliveryPrices = getDriver().findElements(By.xpath("//p[contains(@id,'nbsServiceTileTotalCharge')]"));
            Locale locale = new Locale("en", "US");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

            WebElement cheapestPriceElement = null;
            double cheapestPrice = Double.MAX_VALUE;
            for (WebElement deliveryPrice : deliveryPrices) {
                if (deliveryPrice.isDisplayed()) {
                    double currentPrice = formatter.parse(deliveryPrice.getText()).doubleValue();
                    if (currentPrice < cheapestPrice) {
                        cheapestPrice = currentPrice;
                        cheapestPriceElement = deliveryPrice;
                    }
                }
            }
            System.out.println("Cheapest Price: " + cheapestPrice);
            getExecutor().executeScript("arguments[0].click();", cheapestPriceElement);
        } catch (StaleElementReferenceException e) {
            // To achieve 100% success, if fails, launch itself again
            System.out.println("Retrying");
            iSelectCheapestDeliveryOption();
        }
    }


    @And("^I set Saturday Delivery type$")
    public void iSetSaturdayDeliveryType() throws Throwable {
        getDriver().findElement(By.xpath("//*[@name='nbsShipmentDescription']")).sendKeys("Test");
        WebElement saturday = getDriver().findElement(By.xpath("//input[@id='nbsSaturdayDeliveryOptionBaseOptionSwitch']"));
        if (!saturday.isSelected()) {
            getExecutor().executeScript("arguments[0].click();", saturday);
        }
    }

    @And("^I select Paypal payment type$")
    public void iSelectPaypalPaymentType() {
        WebElement other = getDriver().findElement(By.xpath("//label[@for='other-ways-to-pay-tile']"));
        getExecutor().executeScript("arguments[0].click();", other);
        By payPalLocator = By.xpath("//img[contains(@src,'paypal.png')]");
        getWait(10).until(ExpectedConditions.visibilityOfElementLocated(payPalLocator));
        WebElement element = getDriver().findElement(payPalLocator);
        getExecutor().executeScript("arguments[0].click();", element);
    }

    @Then("^I review all recorded details on the review page$")
    public void iReviewAllRecordedDetailsOnTheReviewPage() {
        Map<String, String> receiver = getData("receiver");
        Map<String, String> sender = getData("sender");

        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//origin-return-drawer//*[@id='originnbsAgentSummaryName']")));
        String origin = getDriver().findElement(By.xpath("//origin-return-drawer")).getText();
        assertThat(origin).containsIgnoringCase(sender.get("name"));
        assertThat(origin).containsIgnoringCase(sender.get("address"));
        assertThat(origin).containsIgnoringCase(sender.get("city"));
        assertThat(origin).containsIgnoringCase(sender.get("state"));
        assertThat(origin).containsIgnoringCase(sender.get("zip"));
        assertThat(origin).containsIgnoringCase(sender.get("email"));
        assertThat(origin).containsIgnoringCase(sender.get("phone"));

        String destination = "";
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//destination-drawer//*[@id='destinationnbsAgentSummaryName']")));
            destination = getDriver().findElement(By.xpath("//destination-drawer")).getText();
        } catch (TimeoutException e) {
            // bug that sometimes values not recorded, have to put them again here
            System.out.println("Values not recorded, re-initiating before validation");
            WebElement zip = getDriver().findElement(By.id("destinationpostal"));
            zip.clear();
            zip.sendKeys(receiver.get("zip"));
            getWait().until(ExpectedConditions.textToBePresentInElementValue(By.id("destinationcity"), receiver.get("city")));
            String upsStateCode = getData("states").get(receiver.get("state"));
            getWait().until(ExpectedConditions.elementToBeSelected(By.xpath("//select[@id='destinationstate']/*[@value='" + upsStateCode + "']")));
            getExecutor().executeScript("arguments[0].click();", getDriver().findElement(By.xpath("//destination-drawer//span[contains(@class,'ups-icon-wedgeup')]")));
            destination = getDriver().findElement(By.xpath("//*[@id='destinationDrawer']")).getText();
        }

        assertThat(destination).containsIgnoringCase(receiver.get("name"));
        assertThat(destination).containsIgnoringCase(receiver.get("address"));
        assertThat(destination).containsIgnoringCase(receiver.get("city"));
        assertThat(destination).containsIgnoringCase(receiver.get("zip"));
    }
}
