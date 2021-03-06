package org.lemanoman.contas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
			"classpath:/static/"};

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("/login");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/sobre").setViewName("sobre");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern("/static/**")) {
			registry.addResourceHandler("/static/**").addResourceLocations(
					CLASSPATH_RESOURCE_LOCATIONS);
		}
	}
}
