-- CUSTOMERS (15)

INSERT INTO customers (name, email, created_at)
VALUES ('Ana Souza', 'ana.souza@email.com', NOW()),
       ('Bruno Lima', 'bruno.lima@email.com', NOW()),
       ('Carlos Mendes', 'carlos.mendes@email.com', NOW()),
       ('Daniela Rocha', 'daniela.rocha@email.com', NOW()),
       ('Eduardo Silva', 'eduardo.silva@email.com', NOW()),
       ('Fernanda Alves', 'fernanda.alves@email.com', NOW()),
       ('Gabriel Santos', 'gabriel.santos@email.com', NOW()),
       ('Helena Costa', 'helena.costa@email.com', NOW()),
       ('Igor Martins', 'igor.martins@email.com', NOW()),
       ('Juliana Ribeiro', 'juliana.ribeiro@email.com', NOW()),
       ('Kleber Nunes', 'kleber.nunes@email.com', NOW()),
       ('Larissa Melo', 'larissa.melo@email.com', NOW()),
       ('Marcos Pereira', 'marcos.pereira@email.com', NOW()),
       ('Natália Gomes', 'natalia.gomes@email.com', NOW()),
       ('Otávio Freitas', 'otavio.freitas@email.com', NOW());


-- PRODUCTS (15)

INSERT INTO products (description, price, stock_quantity, created_at)
VALUES ('Notebook Gamer', 4500.00, 50, NOW()),
       ('Mouse Gamer', 250.00, 200, NOW()),
       ('Teclado Mecânico', 450.00, 150, NOW()),
       ('Monitor 24"', 1200.00, 80, NOW()),
       ('Headset', 350.00, 120, NOW()),
       ('SSD 1TB', 600.00, 100, NOW()),
       ('HD 2TB', 500.00, 90, NOW()),
       ('Placa de Vídeo RTX', 3500.00, 40, NOW()),
       ('Memória RAM 16GB', 400.00, 160, NOW()),
       ('Fonte 750W', 650.00, 70, NOW()),
       ('Gabinete', 550.00, 60, NOW()),
       ('Cadeira Gamer', 900.00, 45, NOW()),
       ('Webcam Full HD', 300.00, 110, NOW()),
       ('Microfone USB', 420.00, 75, NOW()),
       ('Controle Xbox', 380.00, 130, NOW());


-- ORDERS (20)

INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (1, TIMESTAMPTZ '2026-01-05 10:15:00+00', 950.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (1, 2, 2, 250.00, 0.00, 500.00),
    (1, 3, 1, 450.00, 0.00, 450.00);

INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (1, TIMESTAMPTZ '2026-02-10 08:00:00+00', 4700.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (2, 1, 1, 4500.00, 0.00, 4500.00),
    (2, 2, 1, 250.00, 50.00, 200.00);

INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (1, TIMESTAMPTZ '2026-02-28 23:59:59+00', 600.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (3, 5, 2, 350.00, 100.00, 600.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (2, TIMESTAMPTZ '2026-01-12 18:40:00+00', 1200.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (4, 4, 1, 1200.00, 0.00, 1200.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (2, TIMESTAMPTZ '2026-03-02 10:10:00+00', 1400.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (5, 6, 1, 600.00, 0.00, 600.00),
    (5, 9, 2, 400.00, 0.00, 800.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (3, TIMESTAMPTZ '2026-02-01 00:00:00+00', 3500.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (6, 8, 1, 3500.00, 0.00, 3500.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (4, TIMESTAMPTZ '2026-02-11 12:30:00+00', 900.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (7, 10, 1, 650.00, 0.00, 650.00),
    (7, 2,  1, 250.00, 0.00, 250.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (5, TIMESTAMPTZ '2026-02-15 07:45:00+00', 1100.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (8, 12, 1, 900.00, 100.00, 800.00),
    (8, 13, 1, 300.00, 0.00, 300.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (5, TIMESTAMPTZ '2026-01-31 23:59:00+00', 500.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (9, 7, 1, 500.00, 0.00, 500.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (6, TIMESTAMPTZ '2026-02-20 16:05:00+00', 1000.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (10, 15, 3, 380.00, 140.00, 1000.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (7, TIMESTAMPTZ '2026-02-25 11:11:00+00', 420.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (11, 14, 1, 420.00, 0.00, 420.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (8, TIMESTAMPTZ '2026-03-05 19:20:00+00', 4000.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (12, 1, 1, 4500.00, 500.00, 4000.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (9, TIMESTAMPTZ '2026-02-12 20:10:00+00', 1050.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (13, 11, 1, 550.00, 0.00, 550.00),
    (13, 2,  2, 250.00, 0.00, 500.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (10, TIMESTAMPTZ '2026-02-03 14:25:00+00', 400.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (14, 9, 1, 400.00, 0.00, 400.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (11, TIMESTAMPTZ '2026-01-20 09:05:00+00', 1000.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (15, 6, 2, 600.00, 200.00, 1000.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (12, TIMESTAMPTZ '2026-02-10 23:30:00+00', 800.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (16, 3, 1, 450.00, 0.00, 450.00),
    (16, 5, 1, 350.00, 0.00, 350.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (13, TIMESTAMPTZ '2026-02-28 10:00:00+00', 1350.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (17, 2,  4, 250.00, 0.00, 1000.00),
    (17, 15, 1, 380.00, 30.00, 350.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (14, TIMESTAMPTZ '2026-03-01 00:00:01+00', 2000.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (18, 4, 2, 1200.00, 400.00, 2000.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (15, TIMESTAMPTZ '2026-02-11 06:20:00+00', 600.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (19, 13, 2, 300.00, 0.00, 600.00);


INSERT INTO orders (customer_id, order_date, total_amount)
VALUES (5, TIMESTAMPTZ '2026-03-04 13:00:00+00', 1450.00);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount, total_price)
VALUES
    (20, 2, 1, 250.00, 0.00, 250.00),
    (20, 3, 1, 450.00, 0.00, 450.00),
    (20, 12, 1, 900.00, 150.00, 750.00);