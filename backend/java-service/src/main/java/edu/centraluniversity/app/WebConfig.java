package edu.centraluniversity.app;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor jwtAuthInterceptor;

    public WebConfig(AuthInterceptor jwtAuthInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns(List.of("/coworkings/**", "/users/**", "/coworkings/**"))
                .excludePathPatterns("/auth/**", "/users");

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // на все пути
                .allowedOrigins("http://localhost:3000") // разрешённый origin
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // какие методы разрешены
                .allowedHeaders("*") // какие заголовки разрешены
                .allowCredentials(true); // разрешаем куки и авторизацию
    }
}
