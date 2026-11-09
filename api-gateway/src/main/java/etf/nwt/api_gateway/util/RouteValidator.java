package etf.nwt.api_gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
/*
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/refresh-token",
            "/auth/logout",
            "/eureka",
            "/api/categories",
            "/api/chairs",
            "/api/chair-variants",
            "/api/color-options",
            "/api/dimensions",
            "/api/materials"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
*/

    public static final List<String> openAuthEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/refresh-token",
            "/auth/logout",
            "/eureka"
    );

    public static final List<String> openGetEndpoints = List.of(
            "/api/categories",
            "/api/chairs",
            "/api/chair-variants",
            "/api/color-options",
            "/api/dimensions",
            "/api/materials",
            "/api/products/uploads"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        boolean isCompletelyOpen = openAuthEndpoints.stream()
                .anyMatch(path::contains);

        boolean isOpenGetRequest = method.equals("GET") && openGetEndpoints.stream()
                .anyMatch(path::contains);

        return !(isCompletelyOpen || isOpenGetRequest);
    };
}