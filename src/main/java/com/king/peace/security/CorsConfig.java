package com.king.peace.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;



@Configuration
public class CorsConfig  {

	  @Bean
	   public CorsConfigurationSource corsConfigurationSource() {
	       CorsConfiguration configuration = new CorsConfiguration();
	       configuration.setAllowedOrigins(List.of("http://localhost:4200",
		   "https://kingpeter1992-peace-app2026.vercel.app")); // Remplacez par l'origine de votre application Angular
	       configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	       configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
	        configuration.setExposedHeaders(List.of("Content-Disposition")); // ✅ Exposer Content-Disposition
	       configuration.setAllowCredentials(true);
	       configuration.setMaxAge(3600L);

	       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	       source.registerCorsConfiguration("/**", configuration);
	       return source;
	   }
}
