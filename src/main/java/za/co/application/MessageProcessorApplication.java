package za.co.application;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class MessageProcessorApplication implements CommandLineRunner{

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorApplication.class);

	private ExecutorService executorService;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CamelContext camelContext;

	@Autowired
	EventProcessor eventProcessor;

	public static void main(String[] args) {

		SpringApplication.run(MessageProcessorApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		// Order of arguments : jms, jdbc, jpa
		if((strings == null) ||
			(strings.length == 0)) {
			throw new Exception("Invalid arguments. Please fix arguments list.");
		}
		LOGGER.info("Arguments:");
		for(String arg : strings) {
			LOGGER.info(arg);
		}
		if(strings[0].equals(ArgumentsEnum.web.name()) ) {
			LOGGER.info("usage: To run please enter the following URI on the browser http://localhost:8080/send/event?name=Test&persistenceType=[param]");
			LOGGER.info("param: jdbc or jpa");
		}
		else if(strings[0].equals(ArgumentsEnum.jms.name()) &&
				(strings.length > 1) &&
				strings[1].equals(ArgumentsEnum.jdbc.name())) {
			camelContext.addRoutes(createMyRoutes(ArgumentsEnum.jdbc.name()));
			camelContext.start();
		}
		else if(strings[0].equals(ArgumentsEnum.jms.name()) &&
				(strings.length > 1) &&
				strings[1].equals(ArgumentsEnum.jpa.name())) {
			camelContext.addRoutes(createMyRoutes(ArgumentsEnum.jpa.name()));
			camelContext.start();
		} else {
			LOGGER.info("usage: java -jar event-processor-0.0.1-SNAPSHOT.jar param1 [param2]");
			LOGGER.info("param1: web or jms");
			LOGGER.info("param2: jdbc or jpa");
			throw new Exception("Invalid arguments. Please fix arguments list.");
		}


	}

	@Bean
	public ExecutorService executorService() {
		executorService = Executors.newCachedThreadPool();
		return executorService;
	}

	@PreDestroy
	public void destroy() {
		if(executorService != null) {
			executorService.shutdown();
		}

		entityManagerFactory().close();
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(false);
		hibernateJpaVendorAdapter.setGenerateDdl(true);
		hibernateJpaVendorAdapter.setDatabase(Database.H2);
		return hibernateJpaVendorAdapter;
	}

	@Bean (name = "entityManagerFactory")
	@Scope(value = "prototype")
	public EntityManagerFactory entityManagerFactory()
	{
		LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
		lef.setDataSource(dataSource);
		lef.setJpaVendorAdapter(jpaVendorAdapter());
		lef.setPackagesToScan("za.co.application");
		lef.afterPropertiesSet(); // It will initialize EntityManagerFactory object otherwise below will return null
		return lef.getObject();
	}


	public RouteBuilder createMyRoutes(String persistenceType) throws Exception {
		String queueIn = "jms:queue:test/queue";
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("timer://helloTimer?repeatCount=1&delay=-1")
						.process(new Processor() {
							@Override
							public void process(Exchange exchange) throws Exception {
								exchange.getIn().setBody(persistenceType);
							}
						}).to(queueIn);

				from(queueIn + "?concurrentConsumers=20&asyncConsumer=true").process(eventProcessor);
			}
		};
	}

}
