package org.springframework.cloud.config.xml;

import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.StubCloudConnectorTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 
 * @author Ramnivas Laddad
 *
 */
public class CloudAllServicesTest extends StubCloudConnectorTest {
	
	@Test(expected=CloudException.class)
	public void noServices() {
		getTestApplicationContext("cloud-all-services.xml");
	}
	
	@Test(expected=CloudException.class)
	public void partialServicesProvisioned() {
		getTestApplicationContext("cloud-all-services.xml", createMysqlService("db"));
	}
	
	@Test
	public void allServicesProvisioned() {
		ApplicationContext testContext = getTestApplicationContext("cloud-all-services.xml", 
																   createMysqlService("mysqlDb"), 
																   createPostgresqlService("postDb"),
																   createMongoService("mongoDb"),
																   createRedisService("redisDb"),
																   createRabbitService("rabbit"));
		
		assertNotNull("Getting service by id", testContext.getBean("mysqlDb"));
		assertNotNull("Getting service by id and type", testContext.getBean("mysqlDb", DataSource.class));		

		assertNotNull("Getting service by id", testContext.getBean("postDb"));
		assertNotNull("Getting service by id and type", testContext.getBean("postDb", DataSource.class));		

		assertNotNull("Getting service by id", testContext.getBean("mongoDb"));
		assertNotNull("Getting service by id and type", testContext.getBean("mongoDb", MongoDbFactory.class));		
		
		assertNotNull("Getting service by id", testContext.getBean("redisDb"));
		assertNotNull("Getting service by id and type", testContext.getBean("redisDb", RedisConnectionFactory.class));		

		assertNotNull("Getting service by id", testContext.getBean("rabbit"));
		assertNotNull("Getting service by id and type", testContext.getBean("rabbit", ConnectionFactory.class));		
	}
	
}
