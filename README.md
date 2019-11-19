![Travis build status](https://travis-ci.org/pousse-cafe/pousse-cafe-spring.svg?branch=master)
![Maven status](https://maven-badges.herokuapp.com/maven-central/org.pousse-cafe-framework/pousse-cafe-spring/badge.svg)

# Pousse-Cafe-Spring

This project provides a bridge between Pousse-Café and Spring. It enables the injection of Pousse-Café components into
Spring beans. For instance, you can use Spring's `@Autowired` annotation to inject one of your Pousse-Café repositories
into a Spring REST controller (i.e. a bean annotated with `@RestController`).

The `RuntimeConfiguration` helper class may be used to configure your Runtime. Below an example of configuration file
to add to your application:

        @Configuration
        @ComponentScan(basePackages = { "poussecafe.spring" }) // Required in order to enable the bridge
        public class YourConfiguration extends RuntimeConfiguration {
        
            @Override
            protected List<Bundle> bundles() {
                MessagingAndStorage messagingAndStorage = new MessagingAndStorage(
                        InternalMessaging.instance(), // Replace with the messaging you chose
                        InternalStorage.instance());  // Replace with the storage you chose
                List<Bundle> bundles = new ArrayList<>();
                // Add your bundles here
                return bundles;
            }
        }

## Runtime configuration properties

`RuntimeConfiguration` configures the Runtime by looking for below properties:

- `poussecafe.core.failOnDeserializationError`: flag telling of deserialization errors should be considered as
failures or not (default is false)
- `poussecafe.core.processingThreads`: the number of processing threads to start for message consumption (default is 1)
- `poussecafe.core.consumptionMaxRetries`: the maximum number of consumption retries in case of collision (default is 50)
- `poussecafe.core.consumptionBackOffCeiling`: the ceiling for back-off algorithm in case of collision (default is 10)
- `poussecafe.core.consumptionBackOffSlotTime`: the slot time for back-off algorithm in case of collision (default is 3.0)
