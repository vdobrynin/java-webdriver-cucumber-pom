package pages;

import static support.TestContext.getDriver;

public class UspsHeader extends Page {

    public void goToLookUpByZip() {
        getDriver().get("https://tools.usps.com/zip-code-lookup.htm?byaddress");
    }
}
