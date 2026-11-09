package etf.nwt.system_events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SystemEventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemEventsApplication.class, args);
	}

}
