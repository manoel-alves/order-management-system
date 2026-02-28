package br.com.manoel.ordermanagement.repository;

import br.com.manoel.ordermanagement.model.Customer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCustomerRepository implements CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Customer save(Customer customer) {

        String sql = """
                INSERT INTO customers (name, email, created_at)
                VALUES (?, ?, ?)
                RETURNING id
                """;

        Long generatedId = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                customer.getName(),
                customer.getEmail(),
                Timestamp.from(customer.getCreatedAt())
        );
        customer.setId(generatedId);

        return customer;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT id, name, email, created_at FROM customers WHERE id = ?";

        try {
            Customer customer = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), id);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Customer> findByName(String name) {
        String sql = """
            SELECT id, name, email, created_at
            FROM customers
            WHERE LOWER(name) LIKE LOWER(?)
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRow(rs),
                "%" + name + "%"
        );
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT id, name, email, created_at FROM customers";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRow(rs)
        );
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer customer = new Customer(
                rs.getString("name"),
                rs.getString("email"),
                rs.getTimestamp("created_at").toInstant()
        );

        customer.setId(rs.getLong("id"));

        return customer;
    }
}