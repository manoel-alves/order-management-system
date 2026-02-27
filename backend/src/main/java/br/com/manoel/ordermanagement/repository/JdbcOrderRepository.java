package br.com.manoel.ordermanagement.repository;

import br.com.manoel.ordermanagement.model.Order;
import br.com.manoel.ordermanagement.model.OrderItem;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        try {

            String sql = """
                    INSERT INTO orders (customer_id, order_date, total_amount)
                    VALUES (?, ?, ?)
                    RETURNING id
                    """;

            Long generatedId = jdbcTemplate.queryForObject(
                    sql,
                    Long.class,
                    order.getCustomerId(),
                    Timestamp.from(order.getOrderDate()),
                    order.getTotalAmount()
            );

            order.setId(generatedId);


            addOrderItems(order.getId(), order.getItems());

            return order;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pedido com itens", e);
        }
    }

    private void addOrderItems(Long orderId, List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            String itemSql = """
                    INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
                    VALUES (?, ?, ?, ?, ?, ?)
                    RETURNING id
                    """;

            Long itemId = jdbcTemplate.queryForObject(
                    itemSql,
                    Long.class,
                    orderId,
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getDiscount(),
                    item.getTotalPrice()
            );
            item.setId(itemId);
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        List<Order> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                return mapOrder(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, id);

        return result.stream().findFirst();
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                return mapOrder(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, customerId);
    }

    @Override
    public List<Order> findByProductId(Long productId) {
        String sql = """
                SELECT o.*
                FROM orders o
                JOIN order_items i ON o.id = i.order_id
                WHERE i.product_id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                return mapOrder(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, productId);
    }

    @Override
    public BigDecimal findTotalAmountByCustomer(Long customerId) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE customer_id = ?";

        return jdbcTemplate.queryForObject(sql, BigDecimal.class, customerId);
    }

    @Override
    public List<Order> findAll() {
        String sql = """
            SELECT id, customer_id, order_date, total_amount
            FROM orders
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                return mapOrder(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Order> findByPeriod(Instant start, Instant end) {
        String sql = "SELECT * FROM orders WHERE order_date BETWEEN ? AND ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                return mapOrder(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, Timestamp.from(start), Timestamp.from(end));
    }

    private Order mapOrder(ResultSet rs) throws SQLException {

        Long customerId = rs.getLong("customer_id");
        List<OrderItem> items = loadOrderItems(rs.getLong("id"));
        Order order = new Order(customerId, rs.getTimestamp("order_date").toInstant(), items);
        order.setId(rs.getLong("id"));

        return order;
    }

    private List<OrderItem> loadOrderItems(Long orderId) {
        String itemsSql = "SELECT * FROM order_items WHERE order_id = ?";

        return jdbcTemplate.query(itemsSql, (rsItem, rowNum) -> {

                OrderItem item = new OrderItem(
                        rsItem.getLong("product_id"),
                        rsItem.getInt("quantity"),
                        rsItem.getBigDecimal("unit_price"),
                        rsItem.getBigDecimal("discount")
                );

                item.setId(rsItem.getLong("id"));
                return item;

        }, orderId);
    }
}