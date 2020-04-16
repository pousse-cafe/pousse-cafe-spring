package poussecafe.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

public class PousseCafeProperties {

    public PousseCafeProperties() {
        properties = new HashMap<>();
        configurationProperties = new HashMap<>();
    }

    private Map<String, Object> properties;

    private Map<String, Object> configurationProperties;

    public Map<String, Object> getConfigurationProperties() {
        return Collections.unmodifiableMap(configurationProperties);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
        readPropertiesFromEnvironment();
    }

    private Environment environment;

    private void readPropertiesFromEnvironment() {
        if(environment instanceof ConfigurableEnvironment) {
            readPropertiesFromConfigurableEnvironment((ConfigurableEnvironment) environment);
        }
    }

    private void readPropertiesFromConfigurableEnvironment(ConfigurableEnvironment environment) {
        int readProperties = 0;
        int readConfigurationEntries = 0;
        for(PropertySource<?> source : environment.getPropertySources()) {
            if(source instanceof EnumerablePropertySource<?>) {
                EnumerablePropertySource<?> enumerableSource = (EnumerablePropertySource<?>) source;
                for(String propertyName : enumerableSource.getPropertyNames()) {
                    if(propertyName.startsWith(POUSSECAFE_PROPERTY_PREFIX)) {
                        Object propertyValue = enumerableSource.getProperty(propertyName);
                        logger.debug("Loading property {}={}", propertyName, propertyValue);
                        properties.put(propertyName, enumerableSource.getProperty(propertyName));
                    }

                    if(propertyName.startsWith(POUSSECAFE_CONFIG_PROPERTY_PREFIX)) {
                        String configurationKey = propertyName.substring(POUSSECAFE_CONFIG_PROPERTY_PREFIX.length());
                        Object configurationValue = enumerableSource.getProperty(propertyName);
                        logger.debug("Loading configuration {}={}", configurationKey, configurationValue);
                        configurationProperties.put(configurationKey, configurationValue);
                    }
                }
            }
        }
        logger.info("Read {} properties and {} configuration entries", readProperties, readConfigurationEntries);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String POUSSECAFE_PROPERTY_PREFIX = "poussecafe.";

    private static final String POUSSECAFE_CONFIG_PROPERTY_PREFIX = "poussecafe.config.";

    public boolean booleanValue(String key, boolean defaultValue) {
        return value(key, Boolean::valueOf, defaultValue);
    }

    private <T> T value(String key, Function<String, T> parser, T defaultValue) {
        Object value = getPropertyFromMapOrEnvironment(key);
        if(value == null) {
            return defaultValue;
        } else {
            return parseIfNecessary(parser, value);
        }
    }

    private Object getPropertyFromMapOrEnvironment(String key) {
        Object value = properties.get(key);
        if(value == null) {
            value = environment.getProperty(key);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private <T> T parseIfNecessary(Function<String, T> parser, Object value) {
        if(value instanceof String) {
            return parser.apply((String) value);
        } else {
            return (T) value;
        }
    }

    public int intValue(String key, int defaultValue) {
        return value(key, Integer::valueOf, defaultValue);
    }

    public double doubleValue(String key, double defaultValue) {
        return value(key, Double::valueOf, defaultValue);
    }
}
