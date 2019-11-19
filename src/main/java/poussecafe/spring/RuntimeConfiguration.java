package poussecafe.spring;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import poussecafe.apm.ApplicationPerformanceMonitoring;
import poussecafe.apm.DefaultApplicationPerformanceMonitoring;
import poussecafe.environment.Bundle;
import poussecafe.processing.MessageConsumptionConfiguration;
import poussecafe.runtime.Runtime;

public abstract class RuntimeConfiguration {

    @Bean
    public Runtime pousseCafeApplicationContext(
            @Value("${poussecafe.core.failOnDeserializationError:false}") String failOnDeserializationError,
            @Value("${poussecafe.core.processingThreads:1}") String processingThreads,
            @Value("${poussecafe.core.consumptionMaxRetries:50}") String consumptionMaxRetries,
            @Value("${poussecafe.core.consumptionBackOffCeiling:10}") String consumptionBackOffCeiling,
            @Value("${poussecafe.core.consumptionBackOffSlotTime:3.0}") String consumptionBackOffSlotTime) {
        return new Runtime.Builder()
            .failOnDeserializationError(Boolean.valueOf(failOnDeserializationError))
            .processingThreads(Integer.parseInt(processingThreads))
            .messageConsumptionConfiguration(new MessageConsumptionConfiguration.Builder()
                    .maxConsumptionRetries(Integer.parseInt(consumptionMaxRetries))
                    .backOffCeiling(Integer.parseInt(consumptionBackOffCeiling))
                    .backOffSlotTime(Double.valueOf(consumptionBackOffSlotTime))
                    .build())
            .applicationPerformanceMonitoring(applicationPerformanceMonitoring())
            .withBundles(bundles())
            .build();
    }

    protected ApplicationPerformanceMonitoring applicationPerformanceMonitoring() {
        return new DefaultApplicationPerformanceMonitoring();
    }

    protected abstract List<Bundle> bundles();
}
