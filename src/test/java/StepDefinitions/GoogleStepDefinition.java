package StepDefinitions;

import Resources.ResourceConstants;
import Resources.ScenarioContext;
import Resources.TestDataBuilder;
import Utilities.ReusableMethods;
import apiautomation.PlacesApiClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.Assert;

import java.io.IOException;

public class GoogleStepDefinition {

    private final ScenarioContext context;
    private final PlacesApiClient placesClient;
    private final TestDataBuilder testDataBuilder = new TestDataBuilder();
    private Response response;

    public GoogleStepDefinition(ScenarioContext context, PlacesApiClient placesClient) {
        this.context = context;
        this.placesClient = placesClient;
    }

    @When("I add a place with {string}, {string} and {int}")
    public void i_add_a_place(String name, String language, Integer accuracy) throws IOException {
        response = placesClient.addPlace(testDataBuilder.addPlacePayload(name, language, accuracy));
        attachTrace("addPlace");
    }

    @Then("the response status code is {int}")
    public void the_response_status_code_is(int status) {
        Assert.assertEquals(status, response.statusCode());
    }

    @Then("the {string} in response body is {string}")
    public void the_in_response_body_is(String key, String value) {
        Assert.assertEquals(value, ReusableMethods.readJson(response, key));
    }

    @And("I verify place_id maps to {string}")
    public void i_verify_place_id_maps_to(String name) throws IOException {
        context.setPlaceId(ReusableMethods.readJson(response, "place_id"));
        response = placesClient.getPlace(context.getPlaceId());
        attachTrace("getPlace");
        Assert.assertEquals(name, ReusableMethods.readJson(response, "name"));
    }

    @When("I delete the place")
    public void i_delete_the_place() throws IOException {
        response = placesClient.deletePlace(testDataBuilder.deleteplacepayload(context.getPlaceId()));
        attachTrace("deletePlace");
    }

    private void attachTrace(String label) {
        String trace = placesClient.drainLog();
        if (context.getScenario() != null) {
            context.getScenario().attach(trace, "text/plain", label);
        }
    }
}