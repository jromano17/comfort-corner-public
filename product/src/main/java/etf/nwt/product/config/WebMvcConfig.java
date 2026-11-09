package etf.nwt.product.config;


import etf.nwt.product.interceptor.SystemEventsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SystemEventsInterceptor systemEventsInterceptor;

    public WebMvcConfig(SystemEventsInterceptor systemEventsInterceptor) {
        this.systemEventsInterceptor = systemEventsInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(systemEventsInterceptor)
                .addPathPatterns("/**");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/api/products/uploads/**")
            //    .addResourceLocations("file:user-photos/");

        String absolutePath = Paths.get("user-photos").toAbsolutePath().toUri().toString();
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }
        System.out.println("\n === SERVING IMAGES FROM: " + absolutePath + " === \n");

        registry.addResourceHandler("/api/products/uploads/**", "/uploads/**")
                .addResourceLocations(absolutePath);
    }

/*
 String uploadDir = System.getProperty("user.dir") + "/user-photos/";

    // Create directory if it doesn't exist
    java.io.File dir = new java.io.File(uploadDir);
    if (!dir.exists()) {
        dir.mkdirs();
    }

    String location = dir.toURI().toString();

    registry.addResourceHandler("/api/products/uploads/**")
            .addResourceLocations(location);*/
}