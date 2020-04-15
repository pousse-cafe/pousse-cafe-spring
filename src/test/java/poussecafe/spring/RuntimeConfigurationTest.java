package poussecafe.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import poussecafe.runtime.Runtime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@EnableConfigurationProperties
public class RuntimeConfigurationTest {

    @Test
    public void runtimeHasExpectedConfiguration() {
        var value = runtime.configuration().value("testKey").orElseThrow();
        assertThat(value, equalTo("testValue"));
    }

    @Autowired
    private Runtime runtime;
}
