package etf.nwt.api_gateway.filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class GatewayCorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // 1. Allow your React/Angular frontend
        corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));

        // 2. Allow all standard HTTP methods, including OPTIONS (critical for preflight)
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS"));

        // 3. Allow all headers (like Authorization for JWTs)
        corsConfig.setAllowedHeaders(List.of("*"));

        // 4. Allow credentials (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);

        // 5. Cache the preflight response for 1 hour to speed up the frontend
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all routes passing through the gateway
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}