package poussecafe.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import poussecafe.runtime.Bundles;

@Configuration
@Import(RuntimeConfiguration.class)
public class TestConfiguration {

    @Bean
    public Bundles bundles() {
        return new Bundles.Builder().build();
    }
}
