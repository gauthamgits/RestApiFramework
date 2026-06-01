package StepDefinitions;

import Resources.ResourceConstants;
import Resources.ScenarioContext;
import Resources.TestDataBuilder;
import Resources.utils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import Utilities.Reusablemethods;

import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class GoogleStepDefinition extends utils {

    private final ScenarioContext context;
    RequestSpecification reqobj;
    Response responsevalue;
    TestDataBuilder tdbboject = new TestDataBuilder();

    public GoogleStepDefinition(ScenarioContext context) {
        this.context = context;
    }

//    @Given("Add place payload ready")
//    public void add_place_payload_ready() throws IOException {
//
//        //reqobj = given().spec(requestSpecBuilder()).body(tdbboject.addPlacePayload());
//
//    }

    @Given("Add place payload with {string}, {string} and {int}")
    public void add_place_payload_with_data(String name, String language, Integer accuracy) throws IOException {

        reqobj = given().spec(requestSpecBuilder()).body(tdbboject.addPlacePayload(name, language, accuracy));

    }
    @When("user calls {string} api with {string} http request")
    public void user_calls_api_with_http_request(String apiname, String methodType) {
        ResourceConstants inputapiname = ResourceConstants.valueOf(apiname);
        ResponseSpecification resspec = new ResponseSpecBuilder().expectStatusCode(200).expectContentType(ContentType.JSON).build();
        if(methodType.equalsIgnoreCase("post")){
        responsevalue = reqobj
                .when().post(inputapiname.getapiname()); }
        else if(methodType.equalsIgnoreCase("get")){
            responsevalue = reqobj
                    .when().get(inputapiname.getapiname());
        }
    }
    @Then("the response status code is {int}")
    public void the_response_status_code_is(int status) {
        Assert.assertEquals(responsevalue.statusCode(),status);

    }
    @Then("the {string} in response body is {string}")
    public void the_in_response_body_is(String key, String value) {
        Assert.assertEquals(Reusablemethods.readjson(responsevalue, key), value);
        //System.out.println("passeijorwgjewpgwd");

    }

    @And("I verify placeid maps to {string} in {string}")
    public void iVerifyPlaceidMapsToname(String name, String apiname) throws IOException {
        context.setPlaceId(Reusablemethods.readjson(responsevalue, "place_id"));
        reqobj = given().spec(requestSpecBuilder()).queryParam("place_id",context.getPlaceId());
        user_calls_api_with_http_request(apiname, "get");
        Assert.assertEquals(Reusablemethods.readjson(responsevalue, "name"), name);
        System.out.println("name is same");
    }

    @Given("Delete payload is ready")
    public void deletePlayloadIsReady() throws IOException {

        reqobj = given().spec(requestSpecBuilder()).body(tdbboject.deleteplacepayload(context.getPlaceId()));
        System.out.println("delete done");
    }


}
