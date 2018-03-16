package za.co.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Contains the logic to execute the business logic as concurrent async tasks.
 * Created by A100286 on 3/15/2018.
 */
public class EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

    public static List<Counter> executeJpa(String runInstance, ExecutorService executorService, EntityManagerFactory entityManagerFactory) throws Exception{
        try {

            for (int i = 0; i < 1000; i++) {

                try {

                    executorService.submit(new EventServiceJpa( runInstance, i, entityManagerFactory));

                } catch (Exception e) {
                    LOGGER.warn("Exception caught: " + e.getMessage());
                    continue;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(executorService.awaitTermination(6, TimeUnit.SECONDS)) {
                LOGGER.info("Threads completed.");
            }

            List<Counter> counterList = entityManagerFactory.createEntityManager().createQuery("SELECT c FROM Counter c").getResultList();

            return counterList;
        }
    }

    public static List<Counter> executeJdbc(String runInstance, ExecutorService executorService, JdbcTemplate jdbcTemplate) throws Exception{
        try {
            for (int i = 0; i < 1000; i++) {

                try {

                    executorService.submit(new EventServiceJdbc( runInstance, i, jdbcTemplate));

                } catch (Exception e) {
                    LOGGER.warn("Exception caught: " + e.getMessage());
                    continue;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {

            if(executorService.awaitTermination(6, TimeUnit.SECONDS)) {
                LOGGER.info("Threads completed.");
            }

        }

        List<Counter> counterList = jdbcTemplate.query("SELECT * FROM counter", new CounterMapper());

        return counterList;
    }
}
