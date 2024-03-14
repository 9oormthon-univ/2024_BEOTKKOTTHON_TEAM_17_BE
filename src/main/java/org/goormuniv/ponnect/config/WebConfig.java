package org.goormuniv.ponnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "https://ponnect.netlify.app", "https://ponnect.netlify.app:80", "https://ponnect.netlify.app:3000")
                .allowedHeaders("authorization", "User-Agent", "Cache-Control", "Content-Type")
                .exposedHeaders("authorization", "User-Agent", "Cache-Control", "Content-Type")
                .allowedMethods("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("/resources/").setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());
    }
}

