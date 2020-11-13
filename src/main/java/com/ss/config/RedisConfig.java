package com.ss.config;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.util.StringValueResolver;

import redis.clients.jedis.JedisPoolConfig;

import com.ss.entities.CountryCode;
import com.ss.entities.InterOperator;

@Configuration
@PropertySource({ "classpath:config/application.properties" })
public class RedisConfig implements EmbeddedValueResolverAware {
	
	private StringValueResolver resolver;
	
	// 配置Jedis连接池
	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(Integer.parseInt(resolver.resolveStringValue("${redis.maxTotal}")));
		jedisPoolConfig.setMaxIdle(Integer.parseInt(resolver.resolveStringValue("${redis.maxIdle}")));
		jedisPoolConfig.setNumTestsPerEvictionRun(Integer.parseInt(resolver.resolveStringValue("${redis.numTestsPerEvictionRun}")));
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Long.parseLong(resolver.resolveStringValue("${redis.timeBetweenEvictionRunsMillis}")));
		jedisPoolConfig.setMinEvictableIdleTimeMillis(Long.parseLong(resolver.resolveStringValue("${redis.minEvictableIdleTimeMillis}")));
		jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(Long.parseLong(resolver.resolveStringValue("${redis.softMinEvictableIdleTimeMillis}")));
		jedisPoolConfig.setMaxWaitMillis(Long.parseLong(resolver.resolveStringValue("${redis.maxWaitMillis}")));
		jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(resolver.resolveStringValue("${redis.testOnBorrow}")));
		jedisPoolConfig.setTestWhileIdle(Boolean.parseBoolean(resolver.resolveStringValue("${redis.testWhileIdle}")));
		jedisPoolConfig.setBlockWhenExhausted(Boolean.parseBoolean(resolver.resolveStringValue("${redis.blockWhenExhausted}")));
		return jedisPoolConfig;
	}
	
	// 配置连接池信息
	@Bean
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
		String hostName = resolver.resolveStringValue("${redis.hostName}");
		Integer port = Integer.parseInt(resolver.resolveStringValue("${redis.port}"));
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(hostName);
		jedisConnectionFactory.setPort(port);
		jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
		return jedisConnectionFactory;
	}
	
	
	// 配置默认的RedisTemplate
	@Bean
	public RedisTemplate<Object, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
		redisTemplate.setDefaultSerializer(serializer);
		return redisTemplate;
	}
	
	// 配置默认的RedisCacheManager
	@Bean
    @Primary
	public RedisCacheManager redisCacheManager(RedisTemplate<Object, Object> redisTemplate) {
		RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        redisCacheManager.setUsePrefix(true);
        return redisCacheManager;
	}
	
	// 配置RedisTemplate模板
	@Bean
	public RedisTemplate<Object, CountryCode> countryCodeRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		RedisTemplate<Object, CountryCode> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		Jackson2JsonRedisSerializer<CountryCode> serializer = new Jackson2JsonRedisSerializer<>(CountryCode.class);
		redisTemplate.setDefaultSerializer(serializer);
		return redisTemplate;
	}
	
	@Bean
	public RedisCacheManager countryCodeRedisCacheManager(RedisTemplate<Object, CountryCode> countryCodeRedisTemplate) {
		RedisCacheManager redisCacheManager = new RedisCacheManager(countryCodeRedisTemplate);
        redisCacheManager.setUsePrefix(true);
        return redisCacheManager;
	}
	
	@Bean
	public RedisTemplate<Object, InterOperator> interOperatorRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		RedisTemplate<Object, InterOperator> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		Jackson2JsonRedisSerializer<InterOperator> serializer = new Jackson2JsonRedisSerializer<>(InterOperator.class);
		redisTemplate.setDefaultSerializer(serializer);
		return redisTemplate;
	}
	
	@Bean
	public RedisCacheManager interOperatorRedisCacheManager(RedisTemplate<Object, InterOperator> countryCodeRedisTemplate) {
		RedisCacheManager redisCacheManager = new RedisCacheManager(countryCodeRedisTemplate);
        redisCacheManager.setUsePrefix(true);
        return redisCacheManager;
	}
	
	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.resolver = resolver;
	}
}
