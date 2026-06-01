package apiautomation;

import Pojo.AddPlaceSerialiser;
import Pojo.AddPlaceSerialiser;
import Pojo.DeletePlaceSerialiser;
import Resources.ResourceConstants;
import Resources.Utils;
import io.restassured.response.Response;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class PlacesApiClient extends Utils {

    public Response addPlace(AddPlaceSerialiser payload) throws IOException {
        return given().spec(requestSpecBuilder()).body(payload)
                .when().post(ResourceConstants.AddplaceAPI.getApiName());
    }

    public Response getPlace(String placeId) throws IOException {
        return given().spec(requestSpecBuilder()).queryParam("place_id", placeId)
                .when().get(ResourceConstants.GetplaceAPI.getApiName());
    }

    public Response deletePlace(DeletePlaceSerialiser payload) throws IOException {
        return given().spec(requestSpecBuilder()).body(payload)
                .when().post(ResourceConstants.DeleteplaceAPI.getApiName());
    }
}