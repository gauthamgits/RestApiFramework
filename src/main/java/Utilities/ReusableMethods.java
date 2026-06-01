package Utilities;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ReusableMethods {

    public static String readJson(Response response, String path){
        String responsestring = response.asString();
        JsonPath jp = new JsonPath(responsestring);
        return jp.getString(path);
    }
}
