package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.*;

public class WdStepDefs {
    @Given("I open workday careers")
    public void iOpenWorkdayCareers() {
        getDriver().get("https://workday.wd5.myworkdayjobs.com/Workday ");
    }

    @When("I select any position")
    public void iSelectAnyPosition() {
        List<WebElement> jobs = getDriver().findElements(By.xpath("//*[@data-automation-id='promptOption']"));
        int index = new Random().nextInt(jobs.size());
        jobs.get(index).click();
    }

    @And("I go to apply with LinkedIn and verify it opens")
    public void iGoToApplyWithLinkedIn() throws InterruptedException {
        WebElement outerFrame = getDriver().findElement(By.xpath("(//iframe[@data-automation-id='applyWithLinkedinFrame'])[1]"));
        getDriver().switchTo().frame(outerFrame);

        By innerFrameLoc = By.xpath("//iframe[contains(@src,'linkedin.com')]");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(innerFrameLoc));
        getDriver().switchTo().frame(getDriver().findElement(innerFrameLoc));

        // record old set of windows to substract later
        Set<String> oldWindows = getDriver().getWindowHandles();

        // click button so new window opens
        WebElement linkedInButton = getDriver().findElement(By.xpath("//button[@id='awli-button-member-logged-out']"));
        getExecutor().executeScript("arguments[0].click();", linkedInButton);

        // record new set after new window opens
        Set<String> newWindows = getDriver().getWindowHandles();

//      Chrome / Firefox approach
//        for (String window : newWindows) {
//            getDriver().switchTo().window(window);
//        }

        // substract old from new, so only one window left
        newWindows.removeAll(oldWindows);

        // switch on only one left window handle from new set
        getDriver().switchTo().window(newWindows.iterator().next());

        String title = getDriver().getTitle();
        assertThat(title).contains("LinkedIn Login");

    }
}
