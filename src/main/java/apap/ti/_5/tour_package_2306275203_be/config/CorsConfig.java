package apap.ti._5.tour_package_2306275203_be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${CORS_ALLOWED_ORIGINS:*}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                List<String> processedOrigins = new ArrayList<>();
                if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                    Arrays.stream(allowedOrigins.split(","))
                            .map(String::trim)
                            .filter(origin -> !origin.isEmpty())
                            .forEach(processedOrigins::add);
                }

                List<String> defaultDevOrigins = List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:5174",
                        "http://localhost:*",
                        "http://127.0.0.1:*"
                );

                for (String devOrigin : defaultDevOrigins) {
                    if (processedOrigins.stream().noneMatch(origin -> origin.equals(devOrigin))) {
                        processedOrigins.add(devOrigin);
                    }
                }

                registry
                        .addMapping("/**")
                        .allowedOriginPatterns(processedOrigins.toArray(String[]::new))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Authorization");
            }
        };
    }
}


