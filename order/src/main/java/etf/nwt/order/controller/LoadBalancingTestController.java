package etf.nwt.order.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/test")
public class LoadBalancingTestController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/call-product")
    public String helloWorld() {
        //String url = "http://product/api/products/hello-world";
        //return restTemplate.getForObject(url, String.class);

        RestTemplate plainRestTemplate = new RestTemplate();
        String url = "http://localhost:8083/api/products/hello-world"; // ovdje cemo specificirati jednu instancu na portu 8083
        return plainRestTemplate.getForObject(url, String.class);
    }
}