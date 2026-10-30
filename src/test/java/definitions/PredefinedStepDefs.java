package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.getDriver;

public class PredefinedStepDefs {
    @Given("I open url {string}")
    public void iOpenUrl(String string) {
        getDriver().get(string);
    }

    @Given("I open local page {string}")
    public void iOpenLocalPage(String page) {
        URL resource = getClass().getClassLoader().getResource("files/" + page);
        if (resource == null) {
            throw new IllegalArgumentException("Local page not found: " + page);
        }
        getDriver().get(resource.toExternalForm());
    }

    @Then("I should see page title as {string}")
    public void iShouldSeePageTitleAs(String title) {
        assertThat(getDriver().getTitle()).isEqualToIgnoringCase(title);
    }

    @Then("I should see page title contains {string}")
    public void iShouldSeePageTitleContains(String title) {
        assertThat(getDriver().getTitle()).contains(title);
    }

    @Then("element with xpath {string} should be present")
    public void elementWithXpathShouldBePresent(String xpath) {
        assertThat(getDriver()
                .findElement(By.xpath(xpath)));
    }

    @When("I type {string} into element with xpath {string}")
    public void iTypeIntoElementWithXpath(String text, String xpath) throws InterruptedException {
        getDriver().findElement(By.xpath(xpath)).sendKeys(text);
    }

    @Then("I click on element using JavaScript with xpath {string}")
    public void iClickOnElementUsingJavaScriptWithXpath(String xpath) {
        WebElement element = getDriver().findElement(By.xpath(xpath));
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", element);
    }

    @Then("I wait for element with xpath {string} to be present")
    public void iWaitForElementWithXpathToBePresent(String xpath) {
        new WebDriverWait(getDriver(), 10, 200)
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    @Then("element with xpath {string} should contain text {string}")
    public void elementWithXpathShouldContainText(String xpath, String text) {
        String actualText = getDriver().findElement(By.xpath(xpath)).getText();
        assertThat(actualText).contains(text);
    }

    @Then("I wait for {int} sec")
    public void iWaitForSec(int sec) throws InterruptedException {
        Thread.sleep(sec * 1000L);
    }

    @And("I resize window to {int} and {int}")
    public void iResizeWindowToAnd(int width, int height) {
        Dimension dimension = new Dimension(width, height);
        getDriver().manage().window().setSize(dimension);
    }

    @Then("element with xpath {string} should be displayed")
    public void elementWithXpathShouldBeDisplayed(String xpath) {
        assertThat(getDriver().findElement(By.xpath(xpath)).isDisplayed()).isTrue();
    }

    @Then("element with xpath {string} should not be displayed")
    public void elementWithXpathShouldNotBeDisplayed(String xpath) {
        assertThat(getDriver().findElement(By.xpath(xpath)).isDisplayed()).isFalse();
    }

    @And("I click on element with xpath {string}")
    public void iClickOnElementWithXpath(String xpath) {
        getDriver()
                .findElement(By.xpath(xpath)).click();
    }

    @Then("I clear the field {string}")
    public void iClearTheField(String xpath) {
        getDriver()
                .findElement(By.xpath(xpath)).clear();
    }

    @Then("element with xpath {string} should not be present")
    public void elementWithXpathShouldNotBePresent(String xpath) {
        assertThat(getDriver().findElements(By.xpath(xpath))).isEmpty();
    }

    @Then("element with xpath {string} should be disabled")
    public void elementWithXpathShouldBeDisabled(String xpath) {
        assertThat(getDriver().findElement(By.xpath(xpath)).isEnabled()).isFalse();
    }

    @Then("element with xpath {string} should be enabled")
    public void elementWithXpathShouldBeEnabled(String xpath) {
        assertThat(getDriver().findElement(By.xpath(xpath)).isEnabled()).isTrue();
    }


    @And("element with xpath {string} should have attribute {string} as {string}")
    public void elementWithXpathShouldHaveAttributeAs(String xpath, String attribute, String attributeValue) {
        assertThat(getDriver().findElement(By.xpath(xpath))
                .getAttribute(attribute)).isEqualTo(attributeValue);
    }

    @Then("element with xpath {string} should be selected")
    public void elementWithXpathShouldBeSelected(String xpath) {
        assertThat(getDriver().findElement(By.xpath(xpath)).isSelected()).isTrue();
    }

    @When("I switch to iframe with xpath {string}")
    public void iSwitchToIframeWithXpath(String xpath) {
        getDriver().switchTo().frame(getDriver().findElement(By.xpath(xpath)));
    }

    @And("I switch to default content")
    public void iSwitchToDefaultContent() {
        getDriver().switchTo().defaultContent();
    }

    @Then("I accept alert")
    public void iAcceptAlert() {
        getDriver().switchTo().alert().accept();
    }

    @Then("I dismiss alert")
    public void iDismissAlert() {
        getDriver().switchTo().alert().dismiss();
    }

    @When("I clear element with xpath {string}")
    public void iClearElementWithXpath(String xpath) {
        getDriver().findElement(By.xpath(xpath)).clear();
    }
}
