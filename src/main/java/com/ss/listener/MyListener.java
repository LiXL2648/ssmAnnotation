package com.ss.listener;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

@WebListener
public class MyListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		
		// 注册字符编码拦截器
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("CharacterEncodingFilter", new CharacterEncodingFilter());
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		characterEncodingFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		
		// 注册POST请求装换为PUT/DELETE请求的filter
		FilterRegistration.Dynamic hiddenHttpMethodFilter = servletContext.addFilter("HiddenHttpMethodFilter", new HiddenHttpMethodFilter());
		hiddenHttpMethodFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		
		
		// 注册servlet: DruidStatView
		ServletRegistration.Dynamic druidStatView = servletContext.addServlet("DruidStatView", new StatViewServlet());
		druidStatView.setInitParameter("loginUsername", "admin");
		druidStatView.setInitParameter("loginPassword", "123456");
		druidStatView.addMapping("/druid/*");
		
		// 注册filter: DruidWebStatFilter
		FilterRegistration.Dynamic druidWebStatFilter = servletContext.addFilter("DruidWebStatFilter", new WebStatFilter());
		druidWebStatFilter.setInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		druidWebStatFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
