package pages;

import org.openqa.selenium.By;

import static support.TestContext.getDriver;

public class LookUpByZip extends Page {
    public void clickFindByAddress() {
        if (getDriver().findElements(By.id("tAddress")).isEmpty()) {
            getDriver().findElement(By.xpath("//a[contains(@class,'zip-code-address')]")).click();
        }
    }
}
