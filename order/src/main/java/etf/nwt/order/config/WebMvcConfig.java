package etf.nwt.order.config;

import etf.nwt.order.interceptor.SystemEventsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
}