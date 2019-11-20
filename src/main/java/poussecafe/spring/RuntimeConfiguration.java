package poussecafe.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import poussecafe.apm.ApplicationPerformanceMonitoring;
import poussecafe.messaging.internal.InternalMessaging;
import poussecafe.processing.MessageConsumptionConfiguration;
import poussecafe.runtime.Bundles;
import poussecafe.runtime.Runtime;
import poussecafe.storage.internal.InternalStorage;

@Configuration
public class RuntimeConfiguration {

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
}
