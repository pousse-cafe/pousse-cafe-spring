package poussecafe.spring;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import poussecafe.apm.ApplicationPerformanceMonitoring;
import poussecafe.messaging.internal.InternalMessaging;
import poussecafe.processing.MessageConsumptionConfiguration;
import poussecafe.runtime.Bundles;
import poussecafe.runtime.Runtime;
import poussecafe.storage.internal.InternalStorage;

import static java.util.stream.Collectors.toMap;

@Configuration
@ComponentScan(basePackageClasses = RuntimeConfiguration.class)
public class RuntimeConfiguration {

    @Bean
    public Runtime pousseCafeApplicationContext(
            @Value("${poussecafe.core.failOnDeserializationError:false}") String failOnDeserializationError,
            @Value("${poussecafe.core.processingThreads:1}") String processingThreads,
            @Value("${poussecafe.core.consumptionMaxRetries:50}") String consumptionMaxRetries,
            @Value("${poussecafe.core.consumptionBackOffCeiling:10}") String consumptionBackOffCeiling,
            @Value("${poussecafe.core.consumptionBackOffSlotTime:3.0}") String consumptionBackOffSlotTime,
            @Autowired Bundles bundles,
            @Autowired(required = false) ApplicationPerformanceMonitoring applicationPerformanceMonitoring,
            @Autowired @Qualifier("configurationProperties") Properties configurationProperties) {
        Runtime.Builder builder = new Runtime.Builder()
            .failOnDeserializationError(Boolean.valueOf(failOnDeserializationError))
            .processingThreads(Integer.parseInt(processingThreads))
            .messageConsumptionConfiguration(new MessageConsumptionConfiguration.Builder()
                    .maxConsumptionRetries(Integer.parseInt(consumptionMaxRetries))
                    .backOffCeiling(Integer.parseInt(consumptionBackOffCeiling))
                    .backOffSlotTime(Double.valueOf(consumptionBackOffSlotTime))
                    .build())
            .withConfiguration(readConfiguration(configurationProperties))
            .withBundles(bundles);
        if(applicationPerformanceMonitoring != null) {
            builder.applicationPerformanceMonitoring(applicationPerformanceMonitoring);
        }
        return builder.build();
    }

    @Bean
    public InternalStorage internalStorage() {
        return InternalStorage.instance();
    }

    @Bean
    public InternalMessaging internalMessaging() {
        return InternalMessaging.instance();
    }

    private Map<String, Object> readConfiguration(Properties properties) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .collect(toMap(entry -> entry.getKey().toString(), Entry::getValue));
    }

    @ConfigurationProperties(prefix = "poussecafe.core.config")
    @Bean
    public Properties configurationProperties() {
        return new Properties();
    }
}
