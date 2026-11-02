package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class QuoteResult extends Page {
    @FindBy(id = "quotePageResult")
    private WebElement result;

    @FindBy(xpath = "//b[@name='password']")
    private WebElement password;

    @FindBy(xpath = "//b[@name='agreedToPrivacyPolicy']")
    private WebElement privacy;

    @FindBy(xpath = "//b[@name]")
    private List<WebElement> resultElements;

    public String getResult() {
        return result.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    public String getPrivacy() {
        return privacy.getText();
    }

    public boolean areAllResultElementsBold() {
        for (WebElement resultElement : resultElements) {
            if (!resultElement.getTagName().equals("b")) {
                return false;
            }
        }
        return true;
    }
}
