package etf.nwt.order;

import etf.nwt.order.models.*;
import etf.nwt.order.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderApplication {


	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}
	@Bean
	@Profile("!test")
	CommandLineRunner initDatabase(
			OrderRepository orderRepository,
			OrderItemRepository orderItemRepository,
			ShipmentRepository shipmentRepository,
			OrderStatusHistoryRepository statusHistoryRepository,
			IncomeRecordRepository incomeRecordRepository
	) {
		return args -> {

			Address billing = new Address();
			billing.setFullName("John Doe");
			billing.setStreet("123 Main St");
			billing.setCity("Sarajevo");
			billing.setPostalCode("71000");
			billing.setCountry("Bosnia");

			Address shipping = new Address();
			shipping.setFullName("John Doe");
			shipping.setStreet("123 Main St");
			shipping.setCity("Sarajevo");
			shipping.setPostalCode("71000");
			shipping.setCountry("Bosnia");

			Order order = new Order();
			order.setUserId(1L);
			order.setCurrentStatus(OrderStatus.PENDING);
			order.setCreatedAt(LocalDateTime.now());
			order.setBillingAddress(billing);
			order.setShippingAddress(shipping);
			order.setTotalPrice(200.0);

			OrderItem item1 = new OrderItem();
			item1.setVariantId(10L);
			item1.setQuantity(2);
			item1.setPriceAtPurchase(50.0);
			item1.setName("Ergo chair small blue");
			order.addItem(item1);

			OrderItem item2 = new OrderItem();
			item2.setVariantId(11L);
			item2.setQuantity(1);
			item2.setPriceAtPurchase(100.0);
			item2.setName("Dining chair large black");
			order.addItem(item2);

			orderRepository.save(order);

			OrderStatusHistory history = new OrderStatusHistory();
			history.setOrder(order);
			history.setStatus(OrderStatus.PENDING);
			history.setChangedAt(LocalDateTime.now());
			history.setChangedBy(1L);
			statusHistoryRepository.save(history);

			Shipment shipment = new Shipment();
			shipment.setOrder(order);
			shipment.setTrackingNumber("TRACK123");
			shipment.setStatus(ShipmentStatus.PREPARING);
			shipment.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(5));
			shipment.setCreateDate(LocalDateTime.now());
			shipmentRepository.save(shipment);

			IncomeRecord income = new IncomeRecord();
			income.setOrderId(order.getId());
			income.setRecordedAt(LocalDateTime.now());
			income.setIncomeAmount(200.0);
			income.setTax(30.0);
			income.setCost(120.0);
			income.setNetIncome(50.0);
			incomeRecordRepository.save(income);

		};
	}
}
