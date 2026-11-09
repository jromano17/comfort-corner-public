package etf.nwt.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"spring.config.import=",
		"eureka.client.enabled=false",
		"spring.cloud.config.fail-fast=false"
})
class OrderApplicationTests {

	@Test
	void contextLoads() {
	}

}
