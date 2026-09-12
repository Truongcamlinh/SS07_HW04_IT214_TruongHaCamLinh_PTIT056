package com.finbank.customer;

import com.finbank.customer.model.Customer;
import com.finbank.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedCustomers(CustomerRepository customerRepository) {
        return args -> {
            if (customerRepository.count() == 0) {
                customerRepository.save(new Customer(null, "1001", "Nguyen Van A", "nguyenvana@finbank.vn"));
                customerRepository.save(new Customer(null, "1002", "Tran Thi B", "tranthib@finbank.vn"));
            }
        };
    }
}

