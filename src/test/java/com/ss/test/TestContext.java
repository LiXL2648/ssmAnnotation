package com.ss.test;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import redis.clients.jedis.JedisPoolConfig;

import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.ss.config.RootConfig;
import com.ss.entities.CountryCode;
import com.ss.service.CountryCodeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RootConfig.class })
public class TestContext {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private MybatisSqlSessionFactoryBean sqlSessionFactory;

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	@Autowired
	private JedisPoolConfig jedisPoolConfig;
	
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;
	
	@Autowired
	private RedisTemplate<Object, CountryCode> redisTemplate;
	
	@Autowired
	private CountryCodeService countryCodeService;
	
	@Test
	public void testRedisTemplate() {
		CountryCode countryCode = countryCodeService.getCountryCode(3L);
		redisTemplate.opsForValue().set("hello", countryCode);
	}
	
	@Test
	public void testJedisConnectionFactory() {
		System.out.println(jedisConnectionFactory);	
		System.out.println(jedisConnectionFactory.getConnection());
	}
	
	@Test
	public void testJedisPoolConfig() {
		System.out.println(jedisPoolConfig);
	}
	
	/*@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Test
	public void testRedisTemplate() {
		System.out.println(redisTemplate.hasKey("a"));
	}*/

	@Test
	public void testSqlSessionTemplate() {
		System.out.println(sqlSessionTemplate);
	}

	@Test
	public void testSqlSessionFactoryBean() throws Exception {
		System.out.println(sqlSessionFactory.getObject().openSession());
	}

	@Test
	public void testDataSource() throws SQLException {
		System.out.println(dataSource);
		System.out.println(dataSource.getConnection());
	}

	@Test
	public void testTtransactionManager() {
		System.out.println(transactionManager);
	}
}
