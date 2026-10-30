package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import static support.TestContext.getDriver;

public class QuoteForm extends Page {

    public QuoteForm() {
        url = "https://skryabin.com/market/quote.html";
    }

    @FindBy(name = "username")
    private WebElement username;

    @FindBy(name = "email")
    private WebElement email;

    @FindBy(id = "password")
    private WebElement password;

    @FindBy(id = "confirmPassword")
    private WebElement confirmPassword;

    @FindBy(id = "name")
    private WebElement name;

    // Dialog fields - begin
    @FindBy(name = "firstName")
    private WebElement firstName;

    @FindBy(name = "middleName")
    private WebElement middleName;

    @FindBy(name = "lastName")
    private WebElement lastName;

    @FindBy(xpath = "//span[text()='Save']")
    private WebElement saveButton;

    // Dialog fields - end
    @FindBy(name = "agreedToPrivacyPolicy")
    private WebElement privacy;

    @FindBy(name = "phone")
    private WebElement phone;

    @FindBy(name = "dateOfBirth")
    private WebElement dateOfBirth;

    @FindBy(name = "countryOfOrigin")
    private WebElement countryOfOrigin;

    @FindBy(name = "allowedToContact")
    private WebElement allowedToContact;

    @FindBy(name = "address")
    private WebElement address;

    @FindBy(name = "carMake")
    private WebElement carMake;

    @FindBy(id = "formSubmit")
    private WebElement submit;

    public void fillUsername(String text) {
        username.sendKeys(text);
    }

    public void fillEmail(String text) {
        email.sendKeys(text);
    }

    public void fillPassword(String text) {
        password.sendKeys(text);
    }

    public void fillConfirmPassword(String text) {
        confirmPassword.sendKeys(text);
    }

    public void fillName(String first, String last) {
        name.click();
        firstName.sendKeys(first);
        lastName.sendKeys(last);
        saveButton.click();
    }

    public void fillName(String first, String middle, String last) {
        name.click();
        firstName.sendKeys(first);
        middleName.sendKeys(middle);
        lastName.sendKeys(last);
        saveButton.click();
    }

    public void acceptPrivacy() {
        clickWithJS(privacy);
    }

    public void fillPhone(String text) {
        phone.sendKeys(text);
    }

    public void fillDateOfBirth(String text) {
        dateOfBirth.sendKeys(text);
    }

    public void selectCountryOfOrigin(String value) {
        new Select(countryOfOrigin).selectByValue(value);
    }

    public void selectGender(String value) {
        clickWithJS(getDriver().findElement(By.xpath("//input[@name='gender' and @value='" + value + "']")));
    }

    public void allowContact() {
        clickWithJS(allowedToContact);
    }

    public void fillAddress(String text) {
        address.sendKeys(text);
    }

    public void selectCarMake(String value) {
        new Select(carMake).selectByValue(value);
    }

    public void submitForm() {
        clickWithJS(submit);

    }


}
