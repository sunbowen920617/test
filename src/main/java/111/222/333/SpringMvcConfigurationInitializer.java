package com.zheman.lock;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.zheman.lock.filter.KeywordsFilter;
import com.zheman.lock.interceptor.AuthenticationInterceptor;
import com.zheman.lock.interceptor.LoginTokenInterceptor;
import com.zheman.lock.interceptor.RegisterStateInterceptor;
import com.zheman.lock.interceptor.VersionInterceptor;

@Configuration
public class SpringMvcConfigurationInitializer extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// registry.addResourceHandler("/static/**").addResourceLocations("/static/");
	}

	@Bean
	LoginTokenInterceptor loginTokenInterceptor() {
		return new LoginTokenInterceptor();
	}

	@Bean
	VersionInterceptor versionInterceptor() {
		return new VersionInterceptor();
	}

	@Bean
	AuthenticationInterceptor authenticationInterceptor() {
		return new AuthenticationInterceptor();
	}

	@Bean
	RegisterStateInterceptor registerStateInterceptor() {
		return new RegisterStateInterceptor();
	}

	/**
	 * 配置过滤器
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean someFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(keywordsFilter());
		registration.addUrlPatterns("/profile/changeprofilenickname", "/feedback/addFeedback");
		registration.setName("keywordsFilter");
		return registration;
	}

	/**
	 * 创建一个bean
	 * 
	 * @return
	 */
	@Bean(name = "keywordsFilter")
	public Filter keywordsFilter() {
		return new KeywordsFilter();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		// 鉴权
//		registry.addInterceptor(authenticationInterceptor()).excludePathPatterns("/privacy", "/protocol", "/help",
//				"/share/sharebyqrcode");
		// 版本控制
//		registry.addInterceptor(versionInterceptor()).excludePathPatterns("/privacy", "/protocol", "/help",
//				"/share/sharebyqrcode");
		// token验证
//		registry.addInterceptor(loginTokenInterceptor()).excludePathPatterns("/resident/checkmobilephone",
//				"/resident/register", "/resident/loginbypassword", "/resident/loginbysmscode", "/resident/validsmscode",
//				"/resident/updatepassword", "/sms/register", "/sms/login", "/sms/updatepassword", "/privacy",
//				"/protocol", "/help", "/share/sharebyqrcode");
		// 用户注册步骤验证
		registry.addInterceptor(registerStateInterceptor()).addPathPatterns("/resident/register", "/resident/loginbypassword",
				"/resident/loginbysmscode", "/resident/register2step1");

	}

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
	}

}
