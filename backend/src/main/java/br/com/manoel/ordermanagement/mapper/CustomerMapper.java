package br.com.manoel.ordermanagement.mapper;

import br.com.manoel.ordermanagement.dto.response.CustomerResponse;
import br.com.manoel.ordermanagement.model.Customer;

public final class CustomerMapper {

    private CustomerMapper() {}

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }
}