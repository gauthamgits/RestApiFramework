package StepDefinitions;

import Resources.ScenarioContext;
import Utilities.ReusableMethods;
import apiautomation.PlacesApiClient;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

import java.io.IOException;

public class Hooks {

    private final ScenarioContext context;
    private final PlacesApiClient placesClient;

    public Hooks(ScenarioContext context, PlacesApiClient placesClient) {
        this.context = context;
        this.placesClient = placesClient;
    }

    @Before(order = 0)
    public void captureScenario(Scenario scenario) {
        context.setScenario(scenario);
    }

    @Before(value = "@deleteplace", order = 1)
    public void bootstrapPlaceForDeletion() throws IOException {
        if (context.getPlaceId() == null) {
            Response addResponse = placesClient.addPlace(
                    new Resources.TestDataBuilder().addPlacePayload("gautham", "mandarin", 98));
            String placeId = ReusableMethods.readJson(addResponse, "place_id");
            context.setPlaceId(placeId);
        }
    }
}