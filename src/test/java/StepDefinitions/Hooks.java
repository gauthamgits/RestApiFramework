package StepDefinitions;



import io.cucumber.java.Before;

import java.io.IOException;

public class Hooks {

    @Before("@deleteplace")
    public void beforeScenario() throws IOException {
        GoogleStepDefinition gsd = new GoogleStepDefinition();
        if(GoogleStepDefinition.placeid ==null){
        gsd.add_place_payload_with_data("gautham", "mandarin", 98);
        gsd.user_calls_api_with_http_request("AddplaceAPI", "post");
        gsd.iVerifyPlaceidMapsToname("gautham", "GetplaceAPI");
        }
    }
}
