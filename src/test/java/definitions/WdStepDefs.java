package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.*;

public class WdStepDefs {
    @Given("I open workday careers")
    public void iOpenWorkdayCareers() {
        getDriver().get("https://workday.wd5.myworkdayjobs.com/Workday");
    }

    @When("I select any position")
    public void iSelectAnyPosition() {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);
        List<WebElement> jobs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("[data-automation-id='jobTitle']")));
        assertThat(jobs).isNotEmpty();
        int index = new Random().nextInt(jobs.size());
        jobs.get(index).click();
    }

    @And("I go to apply with LinkedIn and verify it opens")
    public void iGoToApplyWithLinkedIn() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);

        WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-automation-id='adventureButton']")));
        applyButton.click();

        WebElement outerFrame = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("iframe[data-automation-id='applyWithLinkedIn']")));
        getDriver().switchTo().frame(outerFrame);

        WebElement innerFrame = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("iframe[title='LinkedIn Embedded Content']")));
        getDriver().switchTo().frame(innerFrame);

        WebElement linkedInButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[id='apply-with-linkedin']")));

        String parentWindow = getDriver().getWindowHandle();
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", linkedInButton);

        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        Set<String> handles = getDriver().getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(parentWindow)) {
                getDriver().switchTo().window(handle);
                break;
            }
        }
        assertThat(getDriver().getCurrentUrl()).contains("linkedin.com");
    }
}
