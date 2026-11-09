package etf.nwt.product;

import etf.nwt.product.model.*;
import etf.nwt.product.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}
	@Bean
	@Profile("!test")
	CommandLineRunner initDatabase(
			CategoryRepository categoryRepository,
			ChairRepository chairRepository,
			SupplierRepository supplierRepository,
			MaterialRepository materialRepository,
			ColorOptionRepository colorRepository,
			DimensionRepository dimensionRepository,
			ChairVariantRepository variantRepository
	) {
		return args -> {

			Category office = new Category(null, "Office", "Office chairs", new ArrayList<>());
			Category gaming = new Category(null, "Gaming", "Gaming chairs", new ArrayList<>());
			categoryRepository.saveAndFlush(office);
			categoryRepository.saveAndFlush(gaming);

			Supplier supplier1 = new Supplier(null, "ChairCorp", "contact@chaircorp.com", "USA", null);
			supplierRepository.saveAndFlush(supplier1);

			Chair chair1 = new Chair(null, "ErgoChair", "Comfortable office chair", new BigDecimal("199.99"), office, null, supplier1,new ArrayList<>(List.of("/api/products/uploads/chair-1.jpg","/api/products/uploads/chair-2.jpg")));
			chairRepository.saveAndFlush(chair1);

			Chair chair2 = new Chair(null, "GamerX", "High-back gaming chair", new BigDecimal("299.99"), gaming, null, supplier1, List.of("/api/products/uploads/chair-1.jpg"));
			chairRepository.saveAndFlush(chair2);

			Material leather = new Material(null, "Leather", "Premium leather material");
			Material fabric = new Material(null, "Fabric", "Breathable fabric");
			materialRepository.saveAndFlush(leather);
			materialRepository.saveAndFlush(fabric);

			ColorOption red = new ColorOption(null, "Red", "#FF0000");
			ColorOption black = new ColorOption(null, "Black", "#000000");
			colorRepository.saveAndFlush(red);
			colorRepository.saveAndFlush(black);

			Dimension small = new Dimension(null, "Small", 50.0, 100.0, 50.0, 100.0);
			Dimension large = new Dimension(null, "Large", 60.0, 120.0, 60.0, 100.0);
			dimensionRepository.saveAndFlush(small);
			dimensionRepository.saveAndFlush(large);

			ChairVariant variant1 = new ChairVariant(null, chair1, leather, black, small, new BigDecimal("219.99"), 10,"/api/products/uploads/chair-main.jpg");
			ChairVariant variant2 = new ChairVariant(null, chair2, fabric, red, large, new BigDecimal("329.99"), 5,"/api/products/uploads/chair-side.jpg");
			ChairVariant variant3 = new ChairVariant(null, chair2, leather, black, small, new BigDecimal("329.99"), 5,"/api/products/uploads/chair-2.jpg");
			variantRepository.saveAndFlush(variant1);
			variantRepository.saveAndFlush(variant2);
			variantRepository.saveAndFlush(variant3);

		};
	}
}
