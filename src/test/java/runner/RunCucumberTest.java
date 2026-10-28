package runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Entry point that tells JUnit (and Maven) to run the Cucumber features.
 * Without this class, "mvn test" would not pick up the .feature files.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")                                   // where the .feature files are
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "steps")     // the package with the step definitions
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")  // readable console output
public class RunCucumberTest {
}