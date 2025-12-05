package com.opipo.jaderegent.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /data/** URLs to the local ./data/ directory
        registry.addResourceHandler("/data/**")
                .addResourceLocations("file:data/");
    }
}
