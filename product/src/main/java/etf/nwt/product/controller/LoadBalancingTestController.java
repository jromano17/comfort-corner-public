package etf.nwt.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/products")
public class LoadBalancingTestController {
    @Autowired
    Environment env;
    @GetMapping("/hello-world")
    public String helloWorld() {
        return "Hello from JR and the Product Service running on port: " + env.getProperty("local.server.port");
    }
}