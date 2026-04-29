DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
    user_id VARCHAR(36) NOT NULL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'OPERATOR' NOT NULL CHECK(role IN ('ADMIN', 'MANAGER', 'OPERATOR')),
    email VARCHAR(100) UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS Warehouses;
CREATE TABLE Warehouses (
    warehouse_id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    capacity_sqm REAL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS Zones;
CREATE TABLE Zones (
    zone_id VARCHAR(36) NOT NULL PRIMARY KEY,
    warehouse_id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    zone_type VARCHAR(20) CHECK(zone_type IN ('COLD', 'DRY', 'HAZARDOUS', 'GENERAL')),
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id) ON DELETE CASCADE
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
    location_id VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL CHECK(quantity >= 0),
    last_updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES StorageLocations(location_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS Orders;
CREATE TABLE Orders (
    order_id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL CHECK(type IN ('INBOUND', 'OUTBOUND')),
    status VARCHAR(20) NOT NULL CHECK(status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    total_amount REAL DEFAULT 0.0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    required_date DATETIME,
    completed_at DATETIME,
    notes VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
);

DROP TABLE IF EXISTS OrderItems;
CREATE TABLE OrderItems (
    order_item_id VARCHAR(36) NOT NULL PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INTEGER NOT NULL CHECK(quantity > 0),
    unit_price REAL NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
);
