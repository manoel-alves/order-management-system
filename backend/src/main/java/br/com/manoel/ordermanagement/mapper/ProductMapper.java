package br.com.manoel.ordermanagement.mapper;

import br.com.manoel.ordermanagement.dto.response.ProductResponse;
import br.com.manoel.ordermanagement.model.Product;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt()
        );
    }
}