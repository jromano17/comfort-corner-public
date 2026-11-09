package etf.nwt.product.interceptor;

import etf.nwt.system_events.EventRequest;
import etf.nwt.system_events.EventResponse;
import etf.nwt.system_events.SystemEventsServiceGrpc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SystemEventsInterceptor implements HandlerInterceptor {

    @GrpcClient("system-events-channel")
    private SystemEventsServiceGrpc.SystemEventsServiceBlockingStub systemEventsStub;

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception{

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            String method = request.getMethod();
            String uri = request.getRequestURI();

            String responseType = (response.getStatus() >= 200 && response.getStatus() < 300) ? "SUCCESS" : "FAIL";

            String user = request.getHeader("X-Logged-In-User");
            if (user == null || user.isEmpty()) user = "anonymous";

            EventRequest eventRequest = EventRequest.newBuilder()
                    .setTimestamp(timestamp)
                    .setMicroserviceName("product-service")
                    .setUser(user)
                    .setActionType(method + " " + responseType)
                    .setResourceName(uri)
                    .build();

            EventResponse eventResponse = systemEventsStub.logSystemEvent(eventRequest);

            System.out.println("gRPC Log sent successfully! Server replied: " + eventResponse.getMessage());

        } catch (Exception e) {
            System.err.println("Failed to send gRPC log: " + e.getMessage());
        }
    }
}