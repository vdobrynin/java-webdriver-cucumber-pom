package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static support.TestContext.getDriver;

public class ByAddressForm extends Page {

    public void fillStreetAddress(String street) {
        getDriver().findElement(By.id("tAddress")).sendKeys(street);
    }

    public void fillCity(String city) {
        getDriver().findElement(By.id("tCity")).sendKeys(city);
    }

    public void fillState(String state) {
        new Select(getDriver().findElement(By.id("tState"))).selectByValue(state);
    }

    public void click(WebElement element) {
        element.click();
    }
}
