CREATE SCHEMA IF NOT EXISTS happy_takeaway;

CREATE TABLE happy_takeaway.menus (
  id CHAR(36) PRIMARY KEY,
  created_at bigint NOT NULL,
  updated_at bigint NOT NULL,
  restaurant_id CHAR(36) NOT NULL,
  name VARCHAR(128) NOT NULL
);

CREATE TABLE happy_takeaway.restaurants (
  id CHAR(36) PRIMARY KEY,
  created_at bigint NOT NULL,
  updated_at bigint NOT NULL,
  owner_id VARCHAR(50) NOT NULL,
  name VARCHAR(128) NOT NULL,
  description TEXT,
  phone_number VARCHAR(32) NOT NULL,
  address_code VARCHAR(12) NOT NULL,
  address_line VARCHAR(128) NOT NULL,
  address_lng NUMERIC(10, 7) NOT NULL,
  address_lat NUMERIC(10, 7) NOT NULL,
  active_menu_id CHAR(36),
  CONSTRAINT fk_active_menu FOREIGN KEY (active_menu_id) REFERENCES happy_takeaway.menus(id) ON DELETE SET NULL
);

CREATE TABLE happy_takeaway.menu_items (
  id CHAR(36) PRIMARY KEY,
  created_at bigint NOT NULL,
  updated_at bigint NOT NULL,
  restaurant_id CHAR(36) NOT NULL,
  name VARCHAR(128) NOT NULL,
  cover_image VARCHAR(256),
  description TEXT,
  price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE happy_takeaway.menu_item_association (
  menu_id CHAR(36) NOT NULL,
  menu_item_id CHAR(36) NOT NULL,
  PRIMARY KEY (menu_item_id, menu_id),
  CONSTRAINT fk_menu FOREIGN KEY (menu_id) REFERENCES happy_takeaway.menus(id) ON DELETE CASCADE,
  CONSTRAINT fk_menu_item FOREIGN KEY (menu_item_id) REFERENCES happy_takeaway.menu_items(id) ON DELETE CASCADE
);