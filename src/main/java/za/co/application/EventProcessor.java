package za.co.application;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * Consumes the event message from the JMS queue.
 * The scope of the bean is prototype because the JMS queue is set to messages concurrently in multiple threads.
 * Created by A100286 on 3/13/2018.
 */
@Service
@Scope("prototype")
public class EventProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessor.class);

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Counter> counterList;

    @Override
    public void process(Exchange exchange) throws Exception {
        String runInstance = UUID.randomUUID().toString() + "-" + ArgumentsEnum.jms.name();

        //When receiving a JMS message, Camel converts the JMS TextMessage to type String.
        if(exchange.getIn().getBody() instanceof String) {
            LOGGER.info("Introducing 10s delay");
            Thread.sleep(10000);
        }
        LOGGER.info("Going to run JMS message event processor for persistence type: " + exchange.getIn().getBody());
        if(ArgumentsEnum.jdbc.name().equals(exchange.getIn().getBody())) {
            counterList = EventHandler.executeJdbc(runInstance, executorService, jdbcTemplate);
        } else if(ArgumentsEnum.jpa.name().equals(exchange.getIn().getBody())) {
            counterList = EventHandler.executeJpa(runInstance, executorService, entityManagerFactory);
        } else {
            LOGGER.warn("Invalid persistence type: " + exchange.getIn().getBody());
            throw new Exception("Invalid persistence type: " + exchange.getIn().getBody());
        }

        LOGGER.info(counterList.toString());
    }

    public List<Counter> getCounterList() {
        return counterList;
    }

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
