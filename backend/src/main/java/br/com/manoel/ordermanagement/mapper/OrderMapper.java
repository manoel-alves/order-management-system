package br.com.manoel.ordermanagement.mapper;

import br.com.manoel.ordermanagement.dto.response.OrderResponse;
import br.com.manoel.ordermanagement.model.Order;
import br.com.manoel.ordermanagement.model.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemResponse> items = order.getItems()
                .stream()
                .map(OrderMapper::toItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                items,
                order.getTotalAmount(),
                order.getOrderDate()
        );
    }

    private static OrderResponse.OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderResponse.OrderItemResponse(
                item.getProductId(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getDiscount(),
                item.getTotalPrice()
        );
    }
}