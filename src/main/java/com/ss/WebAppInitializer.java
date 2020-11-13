package com.ss;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.ss.config.AppConfig;
import com.ss.config.RootConfig;

public class WebAppInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {
	
	//获取跟容器的配置类（Spring配置文件）：父容器
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { RootConfig.class };
	}

	// 获取web容器的配置类（SpringMVC配置文件）：子容器
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { AppConfig.class };
	}

	//读取DispatcherServlet的映射信息，“/”拦截所有请求，（web.xml中SpringMVC的DispatcherServlet拦截的请求）
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}
