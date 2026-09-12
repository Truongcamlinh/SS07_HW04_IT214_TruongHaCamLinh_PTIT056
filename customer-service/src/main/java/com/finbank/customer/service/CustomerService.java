package com.finbank.customer.service;

import com.finbank.customer.dto.CustomerResponse;
import com.finbank.customer.model.Customer;
import com.finbank.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse getByAccountNumber(String accountNumber) {
        Customer customer = customerRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay khach hang cua tai khoan " + accountNumber));
        return new CustomerResponse(customer.getAccountNumber(), customer.getFullName(), customer.getEmail());
    }
}

