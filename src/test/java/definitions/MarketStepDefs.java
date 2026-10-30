package definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import pages.QuoteForm;
import pages.QuoteResult;

import static org.assertj.core.api.Assertions.assertThat;

public class MarketStepDefs {
    @Given("I navigate to {string} page")
    public void iNavigateToPage(String page) {
        switch (page) {
            case "quote":
                new QuoteForm().open();
                break;
            default:
                throw new RuntimeException("Unknown page: " + page);
        }
    }

    @When("I fill out {string} fields")
    public void iFillOutFields(String scope) {
        if (!scope.equalsIgnoreCase("required")) {
            throw new RuntimeException("Unknown fields scope: " + scope);
        }

        QuoteForm form = new QuoteForm();
        form.fillUsername("skryabin");
        form.fillEmail("slava@skryabin.com");
        form.fillPassword("welcome");
        form.fillConfirmPassword("welcome");
        form.fillName("Slava", "Skryabin");
        form.acceptPrivacy();
    }

    @And("I fill out optional fields")
    public void iFillOutOptionalFields() {
        QuoteForm form = new QuoteForm();
        form.fillPhone("1234567890");
        form.fillDateOfBirth("11/17/1981");
        form.selectCountryOfOrigin("USA");
        form.selectGender("female");
        form.allowContact();
        form.fillAddress("123 Main St, Los Altos, CA 94022");
        form.selectCarMake("Toyota");
    }

    @And("I submit the quote form")
    public void iSubmitTheQuoteForm() {
        new QuoteForm().submitForm();
    }

    @Then("I verify {string} fields")
    public void iVerifyFields(String scope) {
        QuoteResult result = new QuoteResult();
        String resultText = result.getResult();

        switch (scope.toLowerCase()) {
            case "required":
                assertThat(resultText).contains("skryabin");
                assertThat(resultText).contains("slava@skryabin.com");
                assertThat(resultText).contains("Slava Skryabin");
                assertThat(resultText).doesNotContain("welcome");
                assertThat(result.getPrivacy()).isEqualTo("true");
                assertThat(result.getPassword()).isEqualTo("[entered]");
                assertThat(result.areAllResultElementsBold()).isTrue();
                break;
            case "optional":
                assertThat(resultText).contains("1234567890");
                assertThat(resultText).contains("11/17/1981");
                assertThat(resultText).contains("USA");
                assertThat(resultText).contains("female");
                assertThat(resultText).contains("123 Main St, Los Altos, CA 94022");
                assertThat(resultText).contains("Toyota");
                break;
            default:
                throw new RuntimeException("Unknown fields scope: " + scope);
        }
    }
}
