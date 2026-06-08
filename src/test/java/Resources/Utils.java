package Resources;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.io.*;
import java.util.Properties;

public class Utils {
    //public static RequestSpecification reqspec;
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private final ByteArrayOutputStream logCapture = new ByteArrayOutputStream();


    public RequestSpecification requestSpecBuilder() throws IOException {

            log.info("Building request spec for baseUri: {}", getGlobalProperty("baseUrl"));
            PrintStream capture = new PrintStream(logCapture, true);
            return new RequestSpecBuilder()
                    .setBaseUri(getGlobalProperty("baseUrl"))
                    .addQueryParam("key", getGlobalProperty("key"))
                    .addFilter(RequestLoggingFilter.logRequestTo(capture))
                    .addFilter(ResponseLoggingFilter.logResponseTo(capture))
                    .addHeader("Content-Type", "application/json")
                    .build();


    }

    public String drainLog() {
        String captured = logCapture.toString();
        logCapture.reset();
        return captured;
    }

    public static String getGlobalProperty(String key) throws IOException {
        String env = System.getProperty("env", "global");
        String fileName = env + ".properties";
        Properties prop = new Properties();
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileNotFoundException(fileName + " not found on classpath");
            }
            prop.load(is);
        }
        return prop.getProperty(key);
    }


}
