package poussecafe.spring;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import poussecafe.apm.ApplicationPerformanceMonitoring;
import poussecafe.messaging.internal.InternalMessaging;
import poussecafe.processing.MessageConsumptionConfiguration;
import poussecafe.runtime.Bundles;
import poussecafe.runtime.Runtime;
import poussecafe.storage.internal.InternalStorage;

@Configuration
@ComponentScan(basePackageClasses = RuntimeConfiguration.class)
public class RuntimeConfiguration implements EnvironmentAware {

    @Bean
    public Runtime pousseCafeApplicationContext(
            Bundles bundles,
            @Nullable ApplicationPerformanceMonitoring applicationPerformanceMonitoring) {

        boolean failOnDeserializationError = pousseCafeProperties.booleanValue(
                "poussecafe.core.failOnDeserializationError", false);
        int processingThreads = pousseCafeProperties.intValue(
                "poussecafe.core.processingThreads", DEFAULT_PROCESSING_THREADS);

        int consumptionMaxRetries = pousseCafeProperties.intValue(
                "poussecafe.core.consumptionMaxRetries", MessageConsumptionConfiguration.DEFAULT_CONSUMPTION_MAX_RETRIES);
        int consumptionBackOffCeiling = pousseCafeProperties.intValue(
                "poussecafe.core.consumptionBackOffCeiling", MessageConsumptionConfiguration.DEFAULT_CONSUMPTION_BACK_OFF_CEILING);
        double consumptionBackOffSlotTime = pousseCafeProperties.doubleValue(
                "poussecafe.core.consumptionBackOffSlotTime", MessageConsumptionConfiguration.DEFAULT_CONSUMPTION_BACK_OFF_SLOT_TIME);

        Runtime.Builder builder = new Runtime.Builder()
            .failOnDeserializationError(failOnDeserializationError)
            .processingThreads(processingThreads)
            .messageConsumptionConfiguration(new MessageConsumptionConfiguration.Builder()
                    .maxConsumptionRetries(consumptionMaxRetries)
                    .backOffCeiling(consumptionBackOffCeiling)
                    .backOffSlotTime(consumptionBackOffSlotTime)
                    .build())
            .withConfiguration(pousseCafeProperties.getConfigurationProperties())
            .withBundles(bundles);
        if(applicationPerformanceMonitoring != null) {
            builder.applicationPerformanceMonitoring(applicationPerformanceMonitoring);
        }
        return builder.build();
    }

    private static final int DEFAULT_PROCESSING_THREADS = 1;

    @Bean
    public InternalStorage internalStorage() {
        return InternalStorage.instance();
    }

    @Bean
    public InternalMessaging internalMessaging() {
        return InternalMessaging.instance();
    }

    private PousseCafeProperties pousseCafeProperties = new PousseCafeProperties();

    @Override
    public void setEnvironment(Environment environment) {
        pousseCafeProperties.setEnvironment(environment);
    }
}
