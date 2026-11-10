package support;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DebugProbe {
    public static void main(String[] args) throws Exception {
        TestContext.initialize("chrome", true);
        TestContext.getDriver().manage().deleteAllCookies();
        TestContext.getDriver().manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        TestContext.getDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        TestContext.getDriver().manage().window().setSize(new Dimension(1920, 1080));
        try {
            TestContext.getDriver().get("https://tools.usps.com/zip-code-lookup.htm?byaddress");
            Thread.sleep(4000);
            for (int i = 1; i <= 3; i++) {
                WebElement st = TestContext.getDriver().findElement(By.id("tAddress"));
                st.clear();
                st.sendKeys("870 7th Ave");
                TestContext.getDriver().findElement(By.id("tCity")).clear();
                TestContext.getDriver().findElement(By.id("tCity")).sendKeys("New York");
                new Select(TestContext.getDriver().findElement(By.id("tState"))).selectByValue("NY");
                TestContext.getDriver().findElement(By.xpath("//a[@id='zip-by-address']")).click();
                long deadline = System.currentTimeMillis() + 20000;
                String result = "TIMEOUT";
                while (System.currentTimeMillis() < deadline) {
                    List<WebElement> divs = TestContext.getDriver()
                            .findElements(By.id("zipByAddressDiv"));
                    if (!divs.isEmpty() && divs.get(0).isDisplayed()
                            && !divs.get(0).getText().trim().isEmpty()) {
                        result = "OK: " + divs.get(0).getText().replaceAll("\\s+", " ")
                                .substring(0, 60);
                        break;
                    }
                    if (!TestContext.getDriver().findElements(By.xpath(
                            "//div[contains(@class,'server-error') "
                                    + "and not(contains(@class,'hidden'))]")).isEmpty()) {
                        result = "ERR";
                        break;
                    }
                    Thread.sleep(500);
                }
                System.out.println("submit " + i + " -> " + result);
                Thread.sleep(2500);
            }
        } finally {
            TestContext.teardown();
        }
    }
}
