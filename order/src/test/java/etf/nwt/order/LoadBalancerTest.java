package etf.nwt.order;

import org.springframework.web.client.RestTemplate;

public class LoadBalancerTest {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:8081/api/test/call-product";

        int count8082 = 0;
        int count8083 = 0;
        int failed = 0;

        System.out.println("--- STARTING 100 REQUESTS (WITH LOAD BALANCING) ---");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            try {
                String response = restTemplate.getForObject(resourceUrl, String.class);

                if (response != null && response.contains("8082")) {
                    count8082++;
                } else if (response != null && response.contains("8083")) {
                    count8083++;
                } else {
                    System.out.println("Unexpected response: " + response);
                }
            } catch (Exception e) {
                failed++;
                System.out.println("Request failed: " + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("--- REPORT ---");
        System.out.println("Total Time: " + totalTime + "ms");
        System.out.println("Responses from Port 8082: " + count8082);
        System.out.println("Responses from Port 8083: " + count8083);
        System.out.println("Failed: " + failed);
    }
}