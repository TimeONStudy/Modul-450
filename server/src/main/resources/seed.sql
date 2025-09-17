-- Categories
INSERT INTO categories (id, name) VALUES
                                      ('cat-1','Novel'),
                                      ('cat-2','Comics');

-- Users  (avoid reserved word "user" – table is typically "users")
INSERT INTO users (id, name, email, authenticated) VALUES
                                                       ('u1','User One','u1@example.com', TRUE),
                                                       ('u2','User Two','u2@example.com', TRUE);

-- Books (adjust column names to your entity mapping)
-- assuming: name, author, available, rented_date, category_id, rented_by
INSERT INTO books (id, name, author, available, rented_date, category_id, rented_by) VALUES
                                                                                         ('b1','1984','Orwell', TRUE,  NULL,       'cat-1', NULL),
                                                                                         ('b2','Dune','Herbert', FALSE, NOW(),     'cat-1', 'u1'),
                                                                                         ('b3','Watchmen','Moore', TRUE, NULL,     'cat-2', NULL);
