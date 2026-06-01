package StepDefinitions;

import Resources.ResourceConstants;
import Resources.ScenarioContext;
import Resources.TestDataBuilder;
import Resources.Utils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import Utilities.ReusableMethods;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class GoogleStepDefinition extends Utils {

    private final ScenarioContext context;
    RequestSpecification reqobj;
    Response responsevalue;
    TestDataBuilder tdbboject = new TestDataBuilder();

    public GoogleStepDefinition(ScenarioContext context) {
        this.context = context;
    }


    @Given("Add place payload with {string}, {string} and {int}")
    public void add_place_payload_with_data(String name, String language, Integer accuracy) throws IOException {

        reqobj = given().spec(requestSpecBuilder()).body(tdbboject.addPlacePayload(name, language, accuracy));

    }
    @When("user calls {string} api with {string} http request")
    public void user_calls_api_with_http_request(String apiname, String methodType) {
        ResourceConstants inputapiname = ResourceConstants.valueOf(apiname);
        ResponseSpecification resspec = new ResponseSpecBuilder().expectStatusCode(200).expectContentType(ContentType.JSON).build();
        if (methodType.equalsIgnoreCase("post")) {
            responsevalue = reqobj
                    .when().post(inputapiname.getApiName());
        } else if (methodType.equalsIgnoreCase("get")) {
            responsevalue = reqobj
                    .when().get(inputapiname.getApiName());
        }
        String trace = drainLog();
        if (context.getScenario() != null) {
            context.getScenario().attach(trace, "text/plain", apiname + " " + methodType);
        }

    }
    @Then("the response status code is {int}")
    public void the_response_status_code_is(int status) {
        Assert.assertEquals(status,responsevalue.statusCode());

    }
    @Then("the {string} in response body is {string}")
    public void the_in_response_body_is(String key, String value) {
        Assert.assertEquals(value, ReusableMethods.readJson(responsevalue, key));

    }

    @And("I verify placeid maps to {string} in {string}")
    public void iVerifyPlaceidMapsToname(String name, String apiname) throws IOException {
        context.setPlaceId(ReusableMethods.readJson(responsevalue, "place_id"));
        reqobj = given().spec(requestSpecBuilder()).queryParam("place_id",context.getPlaceId());
        user_calls_api_with_http_request(apiname, "get");
        Assert.assertEquals(name, ReusableMethods.readJson(responsevalue, "name"));


    }

    @Given("Delete payload is ready")
    public void deletePlayloadIsReady() throws IOException {

        reqobj = given().spec(requestSpecBuilder()).body(tdbboject.deleteplacepayload(context.getPlaceId()));

    }


}
