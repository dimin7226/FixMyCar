package com.fixmycar.config;

import com.fixmycar.service.VisitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final VisitInterceptor visitInterceptor;

    @Autowired
    public WebConfig(VisitInterceptor visitInterceptor) {
        this.visitInterceptor = visitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(visitInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Путь к вашим API эндпоинтам
                //.allowedOrigins("http://localhost:3001", "http://localhost:3000", "http://192.168.103:3001, http://192.168.103:3000") // URL вашего React-приложения
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}