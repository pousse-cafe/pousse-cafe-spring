package poussecafe.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import poussecafe.runtime.Bundles;
import poussecafe.runtime.Runtime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RuntimeConfigurationTest {

    @Configuration
    @Import(RuntimeConfiguration.class)
    static class InnerConfiguration {

        @Bean
        public Bundles bundles() {
            return new Bundles.Builder().build();
        }

        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            PropertySourcesPlaceholderConfigurer ppc =  new PropertySourcesPlaceholderConfigurer();
            ppc.setLocation(new ClassPathResource("application.properties"));
            return ppc;
        }
    }

    @Test
    public void runtimeHasExpectedConfiguration() {
        var value = runtime.configuration().value("testKey").orElseThrow();
        assertThat(value, equalTo("testValue"));
    }

    @Autowired
    private Runtime runtime;
}
