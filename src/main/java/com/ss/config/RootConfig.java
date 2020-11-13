package com.ss.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageInterceptor;

// Spring容器扫描所有组件排除controller
@ComponentScan(value = "com.ss", excludeFilters = {
		@Filter(type = FilterType.ANNOTATION, classes = { Controller.class, ControllerAdvice.class })
})
// 引入外部资源文件
@PropertySource({ "classpath:config/application.properties" })
// 开启事务管理器
@EnableTransactionManagement
// 扫描所有mapper接口创建代理对象
@MapperScan(value = "com.ss.dao")
// 开启支持缓存的注解
@EnableCaching
public class RootConfig implements EmbeddedValueResolverAware {
	
	private StringValueResolver resolver;
	
	// 配置Druid数据源
	@Bean
	public DataSource dataSource() {
		// 阿里 druid 数据库连接池
		DruidDataSource dataSource = new DruidDataSource();
		// 基本属性 url、user、password
		dataSource.setDriverClassName(resolver.resolveStringValue("${db.driverClassName}"));
		dataSource.setUrl(resolver.resolveStringValue("${db.url}"));
		dataSource.setUsername(resolver.resolveStringValue("${db.username}"));
		dataSource.setPassword(resolver.resolveStringValue("${db.password}"));
		
		// 配置初始化大小、最小、最大
		dataSource.setInitialSize(Integer.parseInt(resolver.resolveStringValue("${db.initialSize}")));
		dataSource.setMinIdle(Integer.parseInt(resolver.resolveStringValue("${db.minIdle}")));
		dataSource.setMaxActive(Integer.parseInt(resolver.resolveStringValue("${db.maxActive}")));
		
		// 配置获取连接等待超时的时间
		dataSource.setMaxWait(Long.parseLong(resolver.resolveStringValue("${db.maxWait}")));
		
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(resolver.resolveStringValue("${db.timeBetweenEvictionRunsMillis}")));
		dataSource.setValidationQuery(resolver.resolveStringValue("${db.validationQuery}"));
		dataSource.setTestOnBorrow(Boolean.parseBoolean(resolver.resolveStringValue("${db.testOnBorrow}")));
		dataSource.setTestOnReturn(Boolean.parseBoolean(resolver.resolveStringValue("${db.testOnReturn}")));
		dataSource.setTestWhileIdle(Boolean.parseBoolean(resolver.resolveStringValue("${db.testWhileIdle}")));
		
		dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(resolver.resolveStringValue("${db.minEvictableIdleTimeMillis}")));
		dataSource.setRemoveAbandoned(Boolean.parseBoolean(resolver.resolveStringValue("${db.removeAbandoned}")));
		dataSource.setRemoveAbandonedTimeout(Integer.parseInt(resolver.resolveStringValue("${db.removeAbandonedTimeout}")));
		dataSource.setLogAbandoned(Boolean.parseBoolean(resolver.resolveStringValue("${db.logAbandoned}")));
		
		// 打开PSCache，并且指定每个连接上PSCache的大小
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(resolver
				.resolveStringValue("${db.maxPoolPreparedStatementPerConnectionSize}")));
		
		// 配置监控统计拦截的filters，去掉后监控界面sql无法统计
		try {
			dataSource.setFilters(resolver.resolveStringValue("${db.filters}"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataSource.setUseGlobalDataSourceStat(Boolean.parseBoolean(resolver.resolveStringValue("${db.useGlobalDataSourceStat}")));
		dataSource.setConnectionProperties(resolver.resolveStringValue("${db.connectionProperties}"));
		
		return dataSource;
	}
	
	// 配置事务管理器
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	// 配置MyBatis
	@Bean
	public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
		sqlSessionFactory.setConfigLocation(resolver.getResource("classpath:config/mybatis-conf.xml"));
		sqlSessionFactory.setDataSource(dataSource);
		try {
			sqlSessionFactory.setMapperLocations(resolver.getResources("classpath:config/mapper/*.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		sqlSessionFactory.setTypeAliasesPackage("com.ss.entities");
		
		// 配置MyBatis的分页插件
		PageInterceptor pageInterceptor = new PageInterceptor();
		Properties perProperties = new Properties();
		perProperties.setProperty("helperDialect", "mysql");
		perProperties.setProperty("reasonable", "true");
		perProperties.setProperty("supportMethodsArguments", "true");
		perProperties.setProperty("params", "countSql");
		perProperties.setProperty("count", "countSql");
		perProperties.setProperty("autoRuntimeDialect", "true");
		pageInterceptor.setProperties(perProperties);
		sqlSessionFactory.setPlugins(new Interceptor[] {pageInterceptor});
		return sqlSessionFactory;
	}
	
	
	// 配置用于执行批量操作的SqlSessionTemplate
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(MybatisSqlSessionFactoryBean sqlSessionFactory) {
		try {
			return new SqlSessionTemplate(sqlSessionFactory.getObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.resolver = resolver;
	}
}
