package poussecafe.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import poussecafe.environment.AggregateServices;
import poussecafe.process.DomainProcess;
import poussecafe.runtime.Runtime;
import poussecafe.storage.Storage;

@Component
public class PousseCafeComponentsWirer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Runtime pousseCafeRuntime;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        AutowireCapableBeanFactory beanFactory = event.getApplicationContext().getAutowireCapableBeanFactory();
        for(AggregateServices services : pousseCafeRuntime.environment().aggregateServices()) {
            logger.debug("Wiring services for entity {}", services.aggregateRootEntityClass().getSimpleName());
            beanFactory.autowireBean(services.repository());
            beanFactory.autowireBean(services.repository().dataAccess());
            beanFactory.autowireBean(services.factory());
        }
        for(DomainProcess process : pousseCafeRuntime.environment().domainProcesses()) {
            logger.debug("Wiring domain process {}", process.getClass().getSimpleName());
            beanFactory.autowireBean(process);
        }
        for(Object service : pousseCafeRuntime.environment().services()) {
            logger.debug("Wiring service {}", service.getClass().getSimpleName());
            beanFactory.autowireBean(service);
        }
        for(Storage storage : pousseCafeRuntime.environment().storages()) {
            logger.debug("Wiring storage {}", storage.getClass().getSimpleName());
            beanFactory.autowireBean(storage.getTransactionRunner());
            beanFactory.autowireBean(storage.getMessageSendingPolicy());
        }
    }

    private Logger logger = LoggerFactory.getLogger(getClass());
}
