CREATE SCHEMA IF NOT EXISTS happy_takeaway;

CREATE TABLE happy_takeaway.orders (
  id VARCHAR(36) PRIMARY KEY,
  created_at bigint NOT NULL,
  updated_at bigint NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  restaurant_id VARCHAR(36) NOT NULL,
  status VARCHAR(32) NOT NULL
);

CREATE TABLE happy_takeaway.line_items (
  id VARCHAR(36) PRIMARY KEY,
  order_id VARCHAR(36) NOT NULL,
  item_id VARCHAR(36) NOT NULL,
  quantity int NOT NULL,
  price NUMERIC(10, 2) NOT NULL,
  CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES happy_takeaway.orders(id) ON DELETE CASCADE
);
