package com.example.bibliobackend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Autoriser toutes les requêtes provenant de localhost:3000
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")  // Autoriser cette origine
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Autoriser les méthodes souhaitées
                .allowedHeaders("*");  // Autoriser tous les en-têtes
    }
}
