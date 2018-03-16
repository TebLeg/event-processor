package za.co.application;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
//@SpringBootTest
@ContextConfiguration(classes = {MessageProcessorApplication.class})
public class MessageProcessorApplicationTests {

	@Autowired
	EventProcessor eventProcessor;

	@Autowired
	EventController eventController;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void no_arguments_failure() throws Exception {

		String[] args = null;
		try {
			new MessageProcessorApplication().run(args);
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Invalid arguments. Please fix arguments list.");
		}
	}

	@Test
	public void invalid_number_of_arguments_failure() throws Exception {

		String[] args = new String[] {"user.txt"};
		try {
			new MessageProcessorApplication().run(args);
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Invalid arguments. Please fix arguments list.");
		}
	}

	@Test
	public void invalid_number_of_arguments_jms_failure() throws Exception {

		String[] args = new String[] {"jms"};
		try {
			new MessageProcessorApplication().run(args);
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Invalid arguments. Please fix arguments list.");
		}
	}

	@Test
	public void run_jms_invalid_persistence_type() throws Exception {

		CamelContext ctx = new DefaultCamelContext();
		Exchange exchange = new DefaultExchange(ctx);
		exchange.getIn().setBody("jdb");

		try {
			eventProcessor.process(exchange);
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Invalid persistence type: jdb");
		}
	}

	@Test
	public void run_jms_jdbc_persistence_type() throws Exception {

		JdbcTemplate jdbcTemplate = eventProcessor.getJdbcTemplate();

		jdbcTemplate.update("delete from counter");
		assertEquals(0,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());

		CamelContext ctx = new DefaultCamelContext();
		Exchange exchange = new DefaultExchange(ctx);
		exchange.getIn().setBody("jdbc");
		eventProcessor.process(exchange);
		assertEquals(1000,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());

	}

	@Test
	public void run_jms_jpa_persistence_type() throws Exception {

		JdbcTemplate jdbcTemplate = eventProcessor.getJdbcTemplate();

		jdbcTemplate.update("delete from counter");
		assertEquals(0,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());

		CamelContext ctx = new DefaultCamelContext();
		Exchange exchange = new DefaultExchange(ctx);
		exchange.getIn().setBody("jpa");
		eventProcessor.process(exchange);
		assertEquals(501,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());

	}

	@Test
	public void run_consume_web_service_invalid_persistence_type() throws Exception {

		JdbcTemplate jdbcTemplate = eventController.getJdbcTemplate();

		jdbcTemplate.update("delete from counter");
		assertEquals(0,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());

		String response = eventController.processEvent("Test", "mysql");
		assertEquals(response, "Invalid persistence type: mysql");

	}

	@Test
	public void run_consume_web_service_jdbc_persistence_type() throws Exception {

		JdbcTemplate jdbcTemplate = eventController.getJdbcTemplate();

		jdbcTemplate.update("delete from counter");
		assertEquals(0,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());

		eventController.processEvent("Test", "jdbc");
		assertEquals(1000,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());
	}

	@Test
	public void run_consume_web_service_jpa_persistence_type() throws Exception {

		JdbcTemplate jdbcTemplate = eventController.getJdbcTemplate();

		jdbcTemplate.update("delete from counter");
		assertEquals(0,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());

		eventController.processEvent("Test", "jpa");
		assertEquals(501,jdbcTemplate.query("SELECT * FROM counter", new CounterMapper()).size());
	}

}
