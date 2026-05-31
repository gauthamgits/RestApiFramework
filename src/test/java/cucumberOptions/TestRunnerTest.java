package cucumberOptions;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/Resources/features",
        glue={"StepDefinitions"},
        tags= "@endtoend",
        plugin = {
                "pretty",                                  // clean console output
                "html:target/cucumber-reports/cucumber.html", // browser report
                "json:target/cucumber-reports/cucumber.json"  // machine-readable report
        },
        monochrome = true)
public class TestRunnerTest {

}
