package za.co.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Uses JPA persistence to persist the counter value.
 * Created by A100286 on 3/13/2018.
 */
//@Service
public class EventServiceJpa implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceJpa.class);

    private int count;
    private String runInstance;
    private EntityManagerFactory entityManagerFactory;


    public EventServiceJpa(String runInstance, int count, EntityManagerFactory entityManagerFactory) {
        this.runInstance = runInstance;
        this.count = count;
        this.entityManagerFactory = entityManagerFactory;
    }

    public EventServiceJpa() {
    }

    @Override
    public void run() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {

            //Thread.sleep(5000); //This sleep represents really complex code that takes 5 seconds to run and cannot be further optimised

            entityManager.getTransaction().begin();

            entityManager.persist(new Counter(runInstance, count));
            entityManager.flush();


            if ((count != 0) && (count % 2) == 0) {
                entityManager.getTransaction().rollback();
                //LOGGER.info("Completed but rolling back doReallyComplexProcess iteration: " + count);
            } else {
                entityManager.getTransaction().commit();

                //LOGGER.info("Completed doReallyComplexProcess iteration: " + count);
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

}
