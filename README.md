![Travis build status](https://travis-ci.org/pousse-cafe/pousse-cafe-spring.svg?branch=master)
![Maven status](https://maven-badges.herokuapp.com/maven-central/org.pousse-cafe-framework/pousse-cafe-spring/badge.svg)

# Pousse-Cafe-Spring

This project provides a bridge between Pousse-Café and Spring. It enables the injection of Pousse-Café components into
Spring beans. For instance, you can use Spring's `@Autowired` annotation to inject one of your Pousse-Café repositories
into a Spring REST controller (i.e. a bean annotated with `@RestController`).

It also enables the injection of Spring beans in some Pousse-Café components:
- Services,
- Repositories,
- Data Accesses,
- Factories,
- `TransactionRunner`,
- `MessageSendingPolicy`.

Note that injecting Spring Beans in Domain components is not recommended as you might mix up non-domain and domain
logic. In some particular cases however, this might be the preferred approach (e.g. when domain services rely on
non-domain features like sending e-mails, etc.).

Below an example of configuration file to add to your application in order to automatically build and start a
Pousse-Café Runtime:

    @Configuration
    @ComponentScan(basePackages = { "poussecafe.spring" }) // Required in order to enable the bridge
    public class YourConfiguration {
    
        @Override
        protected Bundles bundles(Messaging messaging,
                Storage storage) {
            MessagingAndStorage messagingAndStorage = new MessagingAndStorage(messaging, storage);
            return new Bundles.Builder()
                // Register your bundles here using withBundle and use messagingAndStorage
                // when building them
                .build();
        }
    }

Note that if you are using a custom Messaging and/or Storage, you'll have to use the specific types for `bundles`
method's arguments.

Finally, note that a Spring Bean may define custom message listeners (i.e. contain methods annotated with
`@MessageListener`). In that case, extending `MessageListeningBean` automatically registers the listeners upon
Bean's initialization.

## Properties

The following properties are used to customize Pousse-Café Runtime:

- `poussecafe.core.failOnDeserializationError`: flag telling of deserialization errors should be considered as
failures or not (default is false)
- `poussecafe.core.processingThreads`: the number of processing threads to start for message consumption (default is 1)
- `poussecafe.core.consumptionMaxRetries`: the maximum number of consumption retries in case of collision (default is 50)
- `poussecafe.core.consumptionBackOffCeiling`: the ceiling for back-off algorithm in case of collision (default is 10)
- `poussecafe.core.consumptionBackOffSlotTime`: the slot time for back-off algorithm in case of collision (default is 3.0)

## Configure your Maven project

Add the following snippet to your POM:

    <dependency>
        <groupId>org.pousse-cafe-framework</groupId>
        <artifactId>pousse-cafe-spring</artifactId>
        <version>${poussecafe.spring.version}</version>
    </dependency>
