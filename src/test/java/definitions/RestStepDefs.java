package definitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import support.RestWrapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static support.TestContext.getData;

public class RestStepDefs {
    @Given("I login via REST API as {string}")
    public void iLoginViaRESTAPIAs(String role) {
        new RestWrapper().login(getData(role));
    }

    @When("I create via REST API {string} position")
    public void iCreateViaRESTAPIPosition(String type) {
        new RestWrapper()
                .createPosition(getData(type));
    }

    @Then("I verify via REST API new position is in the list")
    public void iVerifyViaRESTAPINewPositionIsInTheList() {
        List<Map<String, Object>> positions = RestWrapper.getPositions();
        Map<String, Object> expected = RestWrapper.getLastPosition();
        Map<String, Object> actual = findById(positions, expected.get("id"));

        assertThat(actual).as("New position should be in the list").isNotNull();
        assertFieldsMatch(expected, actual);
    }

    @When("I update via REST API {string} position")
    public void iUpdateViaRESTAPIPosition(String type) {
        new RestWrapper().updatePosition(getData(type + "_updated"), RestWrapper
                .getLastPosition().get("id"));
    }

    @Then("I verify via REST API new position is updated")
    public void iVerifyViaRESTAPINewPositionIsUpdated() {
        Map<String, Object> expected = RestWrapper.getLastPosition();
        Map<String, Object> actual = RestWrapper.getPositionById(expected.get("id"));

        assertThat(actual).as("Updated position should be available").isNotNull();
        assertFieldsMatch(expected, actual);
    }

    @When("I delete via REST API new position")
    public void iDeleteViaRESTAPINewPosition() {
        RestWrapper.deletePositionById(RestWrapper.getLastPosition().get("id"));
    }

    @Then("I verify via REST API new position is deleted")
    public void iVerifyViaRESTAPINewPositionIsDeleted() {
        List<Map<String, Object>> positions = RestWrapper.getPositions();
        Object id = RestWrapper.getLastPosition().get("id");

        assertThat(findById(positions, id))
                .as("Deleted position should not be in the list")
                .isNull();
    }

    @When("I create via REST API {string} candidate")
    public void iCreateViaRESTAPICandidate(String type) {
        new RestWrapper().createCandidate(getData(type));
    }

    @Then("I verify via REST API new candidate is in the list")
    public void iVerifyViaRESTAPINewCandidateIsInTheList() {
        List<Map<String, Object>> candidates = RestWrapper.getCandidates();
        Map<String, Object> expected = RestWrapper.getLastCandidate();
        Map<String, Object> actual = findById(candidates, expected.get("id"));

        assertThat(actual).as("New candidate should be in the list")
                .isNotNull();
        assertFieldsMatch(expected, actual);
    }

    @When("I update via REST API {string} candidate")
    public void iUpdateViaRESTAPICandidate(String type) {
        new RestWrapper().updateCandidate(getData(type + "_updated"), RestWrapper
                .getLastCandidate().get("id"));
    }

    @Then("I verify via REST API new candidate is updated")
    public void iVerifyViaRESTAPINewCandidateIsUpdated() {
        Map<String, Object> expected = RestWrapper.getLastCandidate();
        Map<String, Object> actual = RestWrapper.getCandidateById(expected.get("id"));

        assertThat(actual).as("Updated candidate should be available").isNotNull();
        assertFieldsMatch(expected, actual);
    }

    @When("I delete via REST API new candidate")
    public void iDeleteViaRESTAPINewCandidate() {
        RestWrapper.deleteCandidateById(RestWrapper.getLastCandidate().get("id"));
    }

    @Then("I verify via REST API new candidate is deleted")
    public void iVerifyViaRESTAPINewCandidateIsDeleted() {
        List<Map<String, Object>> candidates = RestWrapper.getCandidates();
        Object id = RestWrapper.getLastCandidate().get("id");

        assertThat(findById(candidates, id))
                .as("Deleted candidate should not be in the list")
                .isNull();
    }

    private static Map<String, Object> findById(List<Map<String, Object>> items, Object id) {
        for (Map<String, Object> item : items) {
            if (String.valueOf(item.get("id")).equals(String.valueOf(id))) {
                return item;
            }
        }
        return null;
    }

    private static void assertFieldsMatch(Map<String, Object> expected, Map<String, Object> actual) {
        for (String key : expected.keySet()) {
            if (key.equals("password") || key.equals("positionsCount") || key.equals("candidatesCount")) {
                continue;
            }
            assertThat(actual.get(key)).as("Field: " + key)
                    .isEqualTo(expected.get(key));
        }
    }
}
