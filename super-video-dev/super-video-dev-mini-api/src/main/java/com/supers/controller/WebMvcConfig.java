package com.supers.controller;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.supers.Interceptor.WebInterceptor;
import com.supers.Interceptor.ZKCuratorClient;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter implements WebMvcConfigurer {
    private final long MAX_AGE_SECS = 3600;

    @Bean(initMethod = "init")
    public ZKCuratorClient zKCuratorClient() {
    	return new ZKCuratorClient();
    }
    
    @Bean
    public WebInterceptor webInterceptor() {
    	return new WebInterceptor();
    }
    
    @Override
	public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(webInterceptor()).addPathPatterns("/user/**")
    	.addPathPatterns("/video/uploadVideo","/video/uploadVideoCover","/video/likeVideo","/video/unLikeVideo")
    	.excludePathPatterns("/user/videoAndUserDetail");
		super.addInterceptors(registry);
	}

	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/","/swagger-ui.html");
    }

    /**
     * 解决swagger资源问题
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:d:/muke/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    /**
     * 支持跨域访问
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
//        builder.propertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.failOnEmptyBeans(false);
        builder.failOnUnknownProperties(false);
//        builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
    }

}
