package Resources;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import java.io.*;
import java.util.Properties;

public class utils {
    public static RequestSpecification reqspec;
    private static final Logger log = LoggerFactory.getLogger(utils.class);
    private static final ByteArrayOutputStream logCapture = new ByteArrayOutputStream();


    public RequestSpecification requestSpecBuilder() throws IOException {
        if (reqspec == null) {
            log.info("Building request spec for baseUri: {}", getglobalproperties("baseUrl"));
            PrintStream capture = new PrintStream(logCapture, true);
            reqspec = new RequestSpecBuilder()
                    .setBaseUri(getglobalproperties("baseUrl"))
                    .addQueryParam("key", getglobalproperties("key"))
                    .addFilter(RequestLoggingFilter.logRequestTo(capture))
                    .addFilter(ResponseLoggingFilter.logResponseTo(capture))
                    .addHeader("Content-Type", "application/json")
                    .build();
        }
        return reqspec;
    }

    public String drainLog() {
        String captured = logCapture.toString();
        logCapture.reset();
        return captured;
    }

    public static String getglobalproperties(String key) throws IOException {
        String env = System.getProperty("env", "global");
        String fileName = env + ".properties";
        Properties prop = new Properties();
        try (InputStream is = utils.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileNotFoundException(fileName + " not found on classpath");
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
