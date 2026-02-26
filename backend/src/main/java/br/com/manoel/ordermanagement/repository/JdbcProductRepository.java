package br.com.manoel.ordermanagement.repository;

import br.com.manoel.ordermanagement.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcProductRepository implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product save(Product product) {
        String sql = """
                INSERT INTO products (description, price, stock_quantity, created_at)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;

        Long generatedId = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                Timestamp.from(product.getCreatedAt())
        );

        product.setId(generatedId);

        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        List<Product> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRow(rs),
                id
        );

        return result.stream().findFirst();
    }

    @Override
    public List<Product> findByDescription(String description) {
        String sql = """
            SELECT id, description, price, stock_quantity, created_at
            FROM products
            WHERE LOWER(description) LIKE LOWER(?)
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRow(rs),
                "%" + description + "%"
        );
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT id, description, price, stock_quantity, created_at FROM products";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRow(rs)
        );
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product(
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("stock_quantity"),
                rs.getTimestamp("created_at").toInstant()
        );

        product.setId(rs.getLong("id"));

        return product;
    }
}