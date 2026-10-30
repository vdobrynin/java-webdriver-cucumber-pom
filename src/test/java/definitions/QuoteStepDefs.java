package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.*;

public class QuoteStepDefs {
    @Given("I go to {string} page")
    public void iGoToPage(String page) throws InterruptedException {
        switch (page.toLowerCase()) {
            case "google":
                getDriver().get("https://www.google.com/");
                break;
            case "amazon":
                getDriver().get("https://www.amazon.com/");
                break;
            case "ups":
                getDriver().get("https://www.ups.com/us/en/Home.page");
                break;
            case "yahoo":
                getDriver().get("https://www.yahoo.com/");
                break;
            case "quote":
                getDriver().get("https://skryabin.com/market/quote.html");
                break;
            case "usps":
                getDriver().get("https://www.usps.com/");
                break;
            default:
                throw new RuntimeException("Unsupported page: " + page);
        }
    }

    @And("I print page details")
    public void iPrintPageDetails() {
        System.out.println(getDriver().getTitle());
        System.out.println(getDriver().getCurrentUrl());
    }

    @And("I go back and forward, then refresh the page")
    public void iGoBackAndForwardThenRefreshThePage() {
        getDriver().navigate().back();
        getDriver().navigate().forward();
        getDriver().navigate().refresh();
    }

    @When("I change resolution to {string}")
    public void iChangeResolutionTo(String layout) {
        Dimension size;
        switch (layout) {
            case "phone":
                size = new Dimension(400, 1024);
                break;
            case "desktop":
                size = new Dimension(1280, 1024);
                break;
            case "other":
                size = new Dimension(1024, 768);
                break;
            default:
                throw new RuntimeException("Not supported layout: " + layout);
        }
        getDriver().manage().window().setSize(size);
    }

    @When("I fill out required fields")
    public void iFillOutRequiredFields() {
        if (getDriver().getCurrentUrl().contains("/webdriver/html/sample.html")) {
            getDriver().findElement(By.xpath("//*[@name='username']"))
                    .sendKeys("vd2019");
            getDriver().findElement(By.xpath("//*[@name='email']"))
                    .sendKeys("v---a@happy.com");
            getDriver().findElement(By.xpath("//*[@id='password']"))
                    .sendKeys("12345");
            getDriver().findElement(By.xpath("//*[@id='confirmPassword']"))
                    .sendKeys("12345");
            getDriver().findElement(By.xpath("//*[@name='agreedToPrivacyPolicy']"))
                    .click();
            return;
        }

        Map<String, String> customer = getData("customer");
        getDriver().findElement(By.xpath("//input[@name='username']"))
                .sendKeys(customer.get("username"));
        getDriver().findElement(By.xpath("//input[@name='email']"))
                .sendKeys(customer.get("email"));
        getDriver().findElement(By.xpath("//input[@name='password']"))
                .sendKeys(customer.get("password"));
        getDriver().findElement(By.xpath("//input[@name='confirmPassword']"))
                .sendKeys(customer.get("password"));
        getDriver().findElement(By.xpath("//input[@id='name']"))
                .click();
        getDriver().findElement(By.xpath("//input[@id='firstName']"))
                .sendKeys(customer.get("firstName"));
        getDriver().findElement(By.xpath("//input[@id='lastName']"))
                .sendKeys(customer.get("lastName"));
        getDriver().findElement(By.xpath("//span[contains(text(),'Save')]"))
                .click();
        getDriver().findElement(By.xpath("//input[@name='agreedToPrivacyPolicy']"))
                .click();
    }

    @And("I submit the form")
    public void iSubmitTheForm() {
//        getDriver().findElement(By.xpath("//button[@id='formSubmit']")).click();
        WebElement submit = getDriver()
                .findElement(By.xpath("//button[@id='formSubmit']"));
        getExecutor().executeScript("arguments[0].click();", submit);
    }

    @Then("I verify required fields")
    public void iVerifyRequiredFields() {
        Map<String, String> customer = getData("customer");
        By resultLocator = By.id("quotePageResult");
        getWait(10).until(ExpectedConditions.visibilityOfElementLocated(resultLocator));
        getWait(10).until(driver -> !driver
                .findElement(resultLocator).getText().isEmpty());

        String actualResult = getDriver().findElement(resultLocator).getText();
        System.out.println(actualResult);

        assertThat(actualResult).contains(customer.get("username"));
        assertThat(actualResult).contains(customer.get("email"));
        assertThat(actualResult).contains(customer.get("firstName") + " " + customer.get("lastName"));
        assertThat(actualResult).doesNotContain(customer.get("password"));

        String privacyResult = getDriver()
                .findElement(By.xpath("//b[@name='agreedToPrivacyPolicy']")).getText();
        assertThat(privacyResult).contains("true");
//        int windowCount = getDriver().getWindowHandles().size();
//        getDriver().findElement(By.xpath("link")).click();
//        int newWindowCount = getDriver().getWindowHandles().size();
//        assertThat(newWindowCount).isEqualTo(windowCount + 1);
    }

    @And("I print logs to the console")
    public void iPrintLogsToTheConsole() throws InterruptedException {
        Thread.sleep(1000);
        LogEntries logs = getDriver().manage().logs().get("browser");
        for (LogEntry entry : logs) {
            System.out.println(entry);
        }

    }

    @When("I {string} {int}rd party agreement")
    public void iRdPartyAgreement(String action, int arg1) {
        getDriver().findElement(By.xpath("//button[@id='thirdPartyButton']")).click();
        if (action.equals("accept")) {
            getDriver().switchTo().alert().accept();
        } else if (action.equals("dismiss")) {
            getDriver().switchTo().alert().dismiss();
        } else {
            throw new RuntimeException("Unrecognized action: " + action);
        }
    }

    @And("I fill out {string} and {string} in additional info")
    public void iFillOutAndInAdditionalInfo(String name, String phone) throws InterruptedException {
        getDriver().switchTo().frame("additionalInfo");
        getDriver().findElement(By.xpath("//input[@id='contactPersonName']"))
                .sendKeys(name);
        getDriver().findElement(By.xpath("//input[@id='contactPersonPhone']"))
                .sendKeys(phone);
        getDriver().switchTo().defaultContent();
    }

    @And("I verify related documents {string} is present")
    public void iVerifyRelatedDocumentsIsPresent(String document) {
//        Set<String> newWindows = getDriver().getWindowHandles();
//        for (String window : newWindows) {
//            System.out.println(window);
//        }

        String originalWindow = getDriver().getWindowHandle();
        getDriver()
                .findElement(By.xpath("//button[contains(@onclick,'new')]"))
                .click();
        Set<String> allWindows = getDriver().getWindowHandles();
        // switch to last window
        for (String window : allWindows) {
            getDriver().switchTo().window(window);
        }

        String actualDocuments = getDriver()
                .findElement(By.xpath("//ul")).getText();
        assertThat(actualDocuments).contains(document);
        // switch to original window
        getDriver().switchTo().window(originalWindow);
    }

}
