package Resources;

import io.cucumber.java.Scenario;

public class ScenarioContext {

    private String placeId;
    private Scenario scenario;
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }


    public Scenario getScenario() { return scenario; }
    public void setScenario(Scenario scenario) { this.scenario = scenario; }
}
