package br.com.manoel.ordermanagement.repository;

import br.com.manoel.ordermanagement.model.Order;
import br.com.manoel.ordermanagement.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    }

    private void addOrderItems(Long orderId, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) return;

        String itemSql = """
                INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(
                itemSql,
                orderItems,
                orderItems.size(),
                (ps, item) -> {
                    ps.setLong(1, orderId);
                    ps.setLong(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setBigDecimal(4, item.getUnitPrice());
                    ps.setBigDecimal(5, item.getDiscount());
                    ps.setBigDecimal(6, item.getTotalPrice());
                }
        );
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = """
                SELECT
                    o.id           AS order_id,
                    o.customer_id  AS customer_id,
                    o.order_date   AS order_date,
                    o.total_amount AS total_amount,
                    i.id           AS item_id,
                    i.product_id   AS product_id,
                    i.quantity     AS quantity,
                    i.unit_price   AS unit_price,
                    i.discount     AS discount,
                    i.total_price  AS total_price
                FROM orders o
                LEFT JOIN order_items i ON o.id = i.order_id
                WHERE o.id = ?
                ORDER BY o.id, i.id
                """;

        List<Order> orders = queryOrdersWithItems(sql, id);
        return orders.stream().findFirst();
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        String sql = """
                SELECT
                    o.id           AS order_id,
                    o.customer_id  AS customer_id,
                    o.order_date   AS order_date,
                    o.total_amount AS total_amount,
                    i.id           AS item_id,
                    i.product_id   AS product_id,
                    i.quantity     AS quantity,
                    i.unit_price   AS unit_price,
                    i.discount     AS discount,
                    i.total_price  AS total_price
                FROM orders o
                LEFT JOIN order_items i ON o.id = i.order_id
                WHERE o.customer_id = ?
                ORDER BY o.id, i.id
                """;

        return queryOrdersWithItems(sql, customerId);
    }

    @Override
    public List<Order> findByProductId(Long productId) {
        String sql = """
                SELECT
                    o.id           AS order_id,
                    o.customer_id  AS customer_id,
                    o.order_date   AS order_date,
                    o.total_amount AS total_amount,
                    i.id           AS item_id,
                    i.product_id   AS product_id,
                    i.quantity     AS quantity,
                    i.unit_price   AS unit_price,
                    i.discount     AS discount,
                    i.total_price  AS total_price
                FROM orders o
                JOIN order_items i ON o.id = i.order_id
                WHERE i.product_id = ?
                ORDER BY o.id, i.id
                """;

        return queryOrdersWithItems(sql, productId);
    }

    @Override
    public BigDecimal findTotalAmountByCustomer(Long customerId) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE customer_id = ?";

        return jdbcTemplate.queryForObject(sql, BigDecimal.class, customerId);
    }

    @Override
    public List<Order> findAll() {
        String sql = """
                SELECT
                    o.id           AS order_id,
                    o.customer_id  AS customer_id,
                    o.order_date   AS order_date,
                    o.total_amount AS total_amount,
                    i.id           AS item_id,
                    i.product_id   AS product_id,
                    i.quantity     AS quantity,
                    i.unit_price   AS unit_price,
                    i.discount     AS discount,
                    i.total_price  AS total_price
                FROM orders o
                LEFT JOIN order_items i ON o.id = i.order_id
                ORDER BY o.id, i.id
                """;

        return queryOrdersWithItems(sql);
    }

    @Override
    public List<Order> findByPeriod(Instant start, Instant end) {
        String sql = """
                SELECT
                    o.id           AS order_id,
                    o.customer_id  AS customer_id,
                    o.order_date   AS order_date,
                    o.total_amount AS total_amount,
                    i.id           AS item_id,
                    i.product_id   AS product_id,
                    i.quantity     AS quantity,
                    i.unit_price   AS unit_price,
                    i.discount     AS discount,
                    i.total_price  AS total_price
                FROM orders o
                LEFT JOIN order_items i ON o.id = i.order_id
                WHERE o.order_date BETWEEN ? AND ?
                ORDER BY o.id, i.id
                """;

        return queryOrdersWithItems(sql, Timestamp.from(start), Timestamp.from(end));
    }

    private List<Order> queryOrdersWithItems(String sql, Object... args) {
        ResultSetExtractor<List<Order>> extractor = (ResultSet rs) -> {
            Map<Long, OrderRow> rowsByOrderId = new LinkedHashMap<>();

            while (rs.next()) {
                long orderId = rs.getLong("order_id");
                OrderRow row = rowsByOrderId.computeIfAbsent(orderId, id -> {
                    try {
                        return new OrderRow(
                                id,
                                rs.getLong("customer_id"),
                                rs.getTimestamp("order_date").toInstant(),
                                new ArrayList<>()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                Long itemId = (Long) rs.getObject("item_id");
                if (itemId != null) {
                    OrderItem item = new OrderItem(
                            rs.getLong("product_id"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price"),
                            rs.getBigDecimal("discount")
                    );
                    item.setId(itemId);
                    row.items().add(item);
                }
            }

            List<Order> orders = new ArrayList<>(rowsByOrderId.size());
            for (OrderRow row : rowsByOrderId.values()) {
                Order order = new Order(row.customerId(), row.orderDate(), row.items());
                order.setId(row.orderId());
                orders.add(order);
            }
            return orders;
        };

        return jdbcTemplate.query(sql, extractor, args);
    }

    private record OrderRow(Long orderId, Long customerId, Instant orderDate, List<OrderItem> items) {
    }
}