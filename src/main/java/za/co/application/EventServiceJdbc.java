package za.co.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


/**
 * Uses JDBC persistence to persist the counter value.
 * Created by A100286 on 3/13/2018.
 */
//@Service
public class EventServiceJdbc implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceJdbc.class);

    private int count;
    private String runInstance;
    private JdbcTemplate jdbcTemplate;


    public EventServiceJdbc(String runInstance, int count, JdbcTemplate jdbcTemplate) {
        this.runInstance = runInstance;
        this.count = count;
        this.jdbcTemplate = jdbcTemplate;
    }

    public EventServiceJdbc() {
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000); //This sleep represents really complex code that takes 5 seconds to run and cannot be further optimised

            jdbcTemplate.update("insert into counter (runid, count) " + "values(?, ?)",
                    new Object[] {
                            runInstance, count
                    });

            //LOGGER.info("Completed doReallyComplexProcess iteration: " + count);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
