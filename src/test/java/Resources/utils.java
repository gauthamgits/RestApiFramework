package Resources;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.InputStream;

import java.io.*;
import java.util.Properties;

public class utils {
    public static RequestSpecification reqspec;
    public RequestSpecification requestSpecBuilder() throws IOException {
        if(reqspec==null) {
            PrintStream log = new PrintStream(new FileOutputStream("logging.txt"));
            reqspec = new RequestSpecBuilder().setBaseUri(getglobalproperties("baseUrl")).addQueryParam("key", "qaclick123")
                    .addFilter(RequestLoggingFilter.logRequestTo(log))
                    .addFilter(ResponseLoggingFilter.logResponseTo(log))
                    .addHeader("Content-Type", "application/json").build();
            return reqspec;
        }
        return reqspec;
    }

    public static String getglobalproperties(String key) throws IOException {
        Properties prop = new Properties();
        try (InputStream is = utils.class.getClassLoader()
                .getResourceAsStream("global.properties")) {
            if (is == null) {
                throw new FileNotFoundException("global.properties not found on classpath");
            }
            prop.load(is);
        }
        return prop.getProperty(key);
    }

    public static String readdjson(Response response, String path){
        String responsestring = response.asString();
        JsonPath jp = new JsonPath(responsestring);
        return jp.getString(path);
    }
}
