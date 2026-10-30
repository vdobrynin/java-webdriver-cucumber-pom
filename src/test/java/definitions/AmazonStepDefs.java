package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.getDriver;
import static support.TestContext.getWait;

public class AmazonStepDefs {
    private static final By SEARCH_RESULTS = By
            .xpath("//*[@data-component-type='s-search-results']");
    @And("I search amazon for {string}")
    public void iSearchAmazonFor(String text) {
        WebElement searchBox = getDriver().findElement(By.id("twotabsearchtextbox"));
        searchBox.clear();
        searchBox.sendKeys(text);
        getDriver().findElement(By.id("nav-search-submit-button")).click();
        getWait(10).until(ExpectedConditions.presenceOfElementLocated(SEARCH_RESULTS));
    }

    @Then("I verify it contains {string}")
    public void iVerifyItContains(String result) {
        String actual = getDriver().findElement(SEARCH_RESULTS).getText();
        assertThat(actual).containsIgnoringCase(result);
    }

}
