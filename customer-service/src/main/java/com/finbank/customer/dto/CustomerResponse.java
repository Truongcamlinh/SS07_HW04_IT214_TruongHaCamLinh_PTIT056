package com.finbank.customer.dto;

public record CustomerResponse(
        String accountNumber,
        String fullName,
        String email
) {
}

