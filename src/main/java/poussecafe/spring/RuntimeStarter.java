package poussecafe.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import poussecafe.runtime.Runtime;

@Component
public class RuntimeStarter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        context.start();
    }

    @Autowired
    private Runtime context;
}
