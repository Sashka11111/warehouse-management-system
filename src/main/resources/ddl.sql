DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
    user_id VARCHAR(36) NOT NULL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'OPERATOR' NOT NULL CHECK(role IN ('ADMIN', 'MANAGER', 'OPERATOR')),
    email VARCHAR(100) UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS Zones;
CREATE TABLE Zones (
    zone_id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    zone_type VARCHAR(20) CHECK(zone_type IN ('COLD', 'DRY', 'HAZARDOUS', 'GENERAL'))
);

DROP TABLE IF EXISTS ProductCategories;
CREATE TABLE ProductCategories (
    category_id VARCHAR(36) NOT NULL PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

DROP TABLE IF EXISTS Suppliers;
CREATE TABLE Suppliers (
    supplier_id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255)
);

DROP TABLE IF EXISTS Products;
CREATE TABLE Products (
    product_id VARCHAR(36) NOT NULL PRIMARY KEY,
    category_id VARCHAR(36),
    supplier_id VARCHAR(36),
    sku VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    unit VARCHAR(20),
    price REAL NOT NULL,
    min_stock_level INTEGER DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES ProductCategories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id) ON DELETE SET NULL
);

DROP TABLE IF EXISTS Inventory;
CREATE TABLE Inventory (
    inventory_id VARCHAR(36) NOT NULL PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    zone_id VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL CHECK(quantity >= 0),
    last_updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (zone_id) REFERENCES Zones(zone_id) ON DELETE CASCADE
);

