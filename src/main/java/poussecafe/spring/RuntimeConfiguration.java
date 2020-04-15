package poussecafe.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import poussecafe.apm.ApplicationPerformanceMonitoring;
import poussecafe.messaging.internal.InternalMessaging;
import poussecafe.processing.MessageConsumptionConfiguration;
import poussecafe.runtime.Bundles;
import poussecafe.runtime.Runtime;
import poussecafe.storage.internal.InternalStorage;

import static java.util.stream.Collectors.toMap;

@Configuration
@ComponentScan(basePackageClasses = RuntimeConfiguration.class)
public class RuntimeConfiguration implements EnvironmentAware {

    @Bean
    public Runtime pousseCafeApplicationContext(
            @Value("${poussecafe.core.failOnDeserializationError:false}") String failOnDeserializationError,
            @Value("${poussecafe.core.processingThreads:1}") String processingThreads,
            @Value("${poussecafe.core.consumptionMaxRetries:50}") String consumptionMaxRetries,
            @Value("${poussecafe.core.consumptionBackOffCeiling:10}") String consumptionBackOffCeiling,
            @Value("${poussecafe.core.consumptionBackOffSlotTime:3.0}") String consumptionBackOffSlotTime,
            @Autowired Bundles bundles,
            @Autowired(required = false) ApplicationPerformanceMonitoring applicationPerformanceMonitoring) {
        Runtime.Builder builder = new Runtime.Builder()
            .failOnDeserializationError(Boolean.valueOf(failOnDeserializationError))
            .processingThreads(Integer.parseInt(processingThreads))
            .messageConsumptionConfiguration(new MessageConsumptionConfiguration.Builder()
                    .maxConsumptionRetries(Integer.parseInt(consumptionMaxRetries))
                    .backOffCeiling(Integer.parseInt(consumptionBackOffCeiling))
                    .backOffSlotTime(Double.valueOf(consumptionBackOffSlotTime))
                    .build())
            .withConfiguration(readConfiguration())
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

    private Map<String, Object> readConfiguration() {
        return runtimeConfigurationProperties().entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .collect(toMap(entry -> entry.getKey().toString(), Entry::getValue));
    }

    @Bean
    public Map<String, Object> runtimeConfigurationProperties() {
        var properties = new HashMap<String, Object>();
        for(PropertySource<?> source : environment.getPropertySources()) {
            if(source instanceof EnumerablePropertySource<?>) {
                EnumerablePropertySource<?> enumerableSource = (EnumerablePropertySource<?>) source;
                for(String propertyName : enumerableSource.getPropertyNames()) {
                    if(propertyName.startsWith(POUSSECAFE_CORE_CONFIG_PROPERTY_PREFIX)) {
                        properties.put(propertyName.substring(POUSSECAFE_CORE_CONFIG_PROPERTY_PREFIX.length()),
                                enumerableSource.getProperty(propertyName));
                    }
                }
            }
        }
        return properties;
    }

    private ConfigurableEnvironment environment;

    private static final String POUSSECAFE_CORE_CONFIG_PROPERTY_PREFIX = "poussecafe.core.config.";

    @Override
    public void setEnvironment(Environment environment) {
        if(environment instanceof ConfigurableEnvironment) {
            this.environment = (ConfigurableEnvironment) environment;
        }
    }
}
