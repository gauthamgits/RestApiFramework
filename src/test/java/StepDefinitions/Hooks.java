package StepDefinitions;



import Resources.ScenarioContext;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.io.IOException;

public class Hooks {

    private final GoogleStepDefinition googleSteps;
    private final ScenarioContext context;

    public Hooks(GoogleStepDefinition googleSteps, ScenarioContext context) {
        this.googleSteps = googleSteps;
        this.context = context;
    }

    @Before(order = 0)
    public void captureScenario(Scenario scenario) {
        context.setScenario(scenario);
    }

    @Before(value = "@deleteplace", order = 1)
    public void beforeScenario() throws IOException {

        if(context.getPlaceId() ==null){
            googleSteps.add_place_payload_with_data("gautham", "mandarin", 98);
            googleSteps.user_calls_api_with_http_request("AddplaceAPI", "post");
            googleSteps.iVerifyPlaceidMapsToname("gautham", "GetplaceAPI");
        }
    }
}
