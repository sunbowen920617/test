package com.zheman.lock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.qcloud.image.ImageClient;
import com.tencent.xinge.XingeApp;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.zheman.lock.dao")
@EnableSwagger2
public class LockApplication {

	public static void main(String[] args) {
		SpringApplication.run(LockApplication.class, args);
	}

	@Value("${tencent.push.resident.appId}")
	Long androidAccessId;
	@Value("${tencent.push.resident.secertKey}")
	String androidSecretKey;

	@Value("${tencent.push.resident.ios.appId}")
	Long iosAccessId;
	@Value("${tencent.push.resident.ios.secertKey}")
	String iosSecretKey;

	@Value("${tencent.push.visitor.appId}")
	Long visitorAccessId;
	@Value("${tencent.push.visitor.secretKey}")
	String visitorSecretKey;

	@Value("${tencent.imageclient.appId}")
	String appId;
	@Value("${tencent.imageclient.secretId}")
	String secretId;
	@Value("${tencent.imageclient.secretKey}")
	String secretKey;

	@Bean("xingeApp")
	public XingeApp xingeApp() {
		XingeApp xingeApp = new XingeApp(androidAccessId, androidSecretKey);
		return xingeApp;
	}

	@Bean("iosXingeApp")
	public XingeApp iosXingeApp() {
		XingeApp xingeApp = new XingeApp(iosAccessId, iosSecretKey);
		return xingeApp;
	}

	@Bean("visitorXingeApp")
	public XingeApp visitorXingeApp() {
		XingeApp xingeApp = new XingeApp(visitorAccessId, visitorSecretKey);
		return xingeApp;
	}

	@Bean("imageClient")
	public ImageClient imageClient() {
		ImageClient imageClient = new ImageClient(appId, secretId, secretKey);
		return imageClient;
	}
	
	

	/**
	 * 配置Druid连接池Spring监控
	 */
	@Bean
	public DruidStatInterceptor druidStatInterceptor() {
		DruidStatInterceptor dsInterceptor = new DruidStatInterceptor();
		return dsInterceptor;
	}

	/**
	 * 配置Druid连接池Spring监控
	 */
	@Bean
	@Scope("prototype")
	public JdkRegexpMethodPointcut jdkRegexpMethodPointcut() {
		JdkRegexpMethodPointcut jdkRegexpMethodPointcut = new JdkRegexpMethodPointcut();
		jdkRegexpMethodPointcut.setPatterns("com.zheman.lock.*");
		return jdkRegexpMethodPointcut;
	}

	/**
	 * 配置Druid连接池Spring监控
	 */
	@Bean
	public DefaultPointcutAdvisor druidStatAdvisor(DruidStatInterceptor druidStatInterceptor,
			JdkRegexpMethodPointcut druidStatPointcut) {
		DefaultPointcutAdvisor defaultPointAdvisor = new DefaultPointcutAdvisor();
		defaultPointAdvisor.setPointcut(druidStatPointcut);
		defaultPointAdvisor.setAdvice(druidStatInterceptor);
		return defaultPointAdvisor;
	}

}
