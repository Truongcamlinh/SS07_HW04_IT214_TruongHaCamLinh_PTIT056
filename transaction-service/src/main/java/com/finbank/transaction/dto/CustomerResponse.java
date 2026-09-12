package com.finbank.transaction.dto;

public record CustomerResponse(
        String accountNumber,
        String fullName,
        String email
) {
}

