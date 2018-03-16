package za.co.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * The web controller class.
 * It contains the high level view of the functionality driven by the request parameters of the URL.
 * The database table contents are printed on the browser page after execution
 * Created by A100286 on 3/14/2018.
 */
@RestController
public class EventController {

    @Autowired
    EventProcessor eventProcessor;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @RequestMapping("/send/event")
    @ResponseBody
    public String processEvent(@RequestParam(value="name") String name, @RequestParam(value="persistenceType") String persistenceType) {
        String response = "Response for event: ";
        List<Counter> counterList = new ArrayList<>();
        try {
            String runInstance = UUID.randomUUID().toString() + "-" + ArgumentsEnum.web.name();
            if(ArgumentsEnum.jdbc.name().equals(persistenceType)) {
                counterList = EventHandler.executeJdbc(runInstance, executorService, jdbcTemplate);
            } else if (ArgumentsEnum.jpa.name().equals(persistenceType)) {
                counterList = EventHandler.executeJpa(runInstance, executorService, entityManagerFactory);
            } else {
                return "Invalid persistence type: " + persistenceType;
            }

            response =  name + ", is: " + counterList.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
