package com.nexuscrm.backend;

import com.nexuscrm.backend.model.Product;
import com.nexuscrm.backend.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedDatabase(ProductRepository productRepository) {
		return args -> {
			if (productRepository.count() == 0) {
				Product p1 = new Product();
				p1.setName("Premium Support Package");
				p1.setPrice(150.0);
				p1.setStock(100);
				p1.setSku("SRV-PREM-01");
				productRepository.save(p1);

				Product p2 = new Product();
				p2.setName("Cloud Storage 1TB");
				p2.setPrice(10.0);
				p2.setStock(500);
				p2.setSku("CST-1TB-01");
				productRepository.save(p2);

				Product p3 = new Product();
				p3.setName("CRM Enterprise License");
				p3.setPrice(500.0);
				p3.setStock(999);
				p3.setSku("CRM-ENT-01");
				productRepository.save(p3);
			}
		};
	}
}
