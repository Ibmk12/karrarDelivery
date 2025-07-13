package com.karrardelivery.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karrardelivery.logging.LoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = { "com.karrardelivery"}, excludeFilters = {
		@ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION),
		@ComponentScan.Filter(value = Configuration.class, type = FilterType.ANNOTATION),
		@ComponentScan.Filter(value = EnableWebMvc.class, type = FilterType.ANNOTATION) })
@EnableAsync
@EnableSpringConfigured
@PropertySource(value = { "classpath:application.properties"})
public class AppConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public ObjectMapper getMapper()
	{
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}

	@Bean
	public LoggingFilter loggingFilter() {
		return new LoggingFilter();
	}

	@Bean
	public FilterRegistrationBean<LoggingFilter> loggingFilterRegistration() {
		FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(loggingFilter());
		registrationBean.addUrlPatterns("/*");  // specify URL patterns
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // set the order of the filter (optional)
		return registrationBean;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(true);
		return messageSource;
	}
}
