package etf.nwt.api_gateway.filter;

import etf.nwt.api_gateway.util.JwtUtil;
import etf.nwt.api_gateway.util.RouteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            // prvo provjeravamo da li uopce treba autorizacija
            if (validator.isSecured.test(exchange.getRequest())) {

                // da li fali header za autorizaciju
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    throw new RuntimeException("Missing authorization header!");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7); // uklanjamo "Bearer " prefiks
                }
                //validacija tokena
                try {

                    System.out.println(authHeader);
                    jwtUtil.validateToken(authHeader);

                    Long userId = jwtUtil.extractUserId(authHeader);
                    String role = jwtUtil.extractRole(authHeader);

                    System.out.println(userId);
                    System.out.println(role);

                    // dodajemo potrebne info u headeru
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .headers(httpHeaders -> {
                                // ukidamo ako postoje neki ubaceni headeri
                                httpHeaders.remove("X-Logged-In-User");
                                httpHeaders.remove("X-Logged-In-Role");

                                // dodajemo pravi header
                                httpHeaders.set("X-Logged-In-User", String.valueOf(userId));
                                httpHeaders.set("X-Logged-In-Role", role);
                            })
                            .build();

                    //saljemo ovakav req
                    return chain.filter(exchange.mutate().request(request).build());

                } catch (Exception e) {
                    System.out.println("Invalid access...!");
                    System.out.println(e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }

            // pusto req ako je sve proslo ok
            return chain.filter(exchange);
        });
    }

    public static class Config {
        //potrebno za spring cloud strukturu
    }
}