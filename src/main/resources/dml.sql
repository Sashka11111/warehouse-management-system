INSERT INTO Users (user_id, username, password, full_name, role, email) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Admin', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'Головний Адміністратор', 'ADMIN', 'admin@warehouse.ua'),
('550e8400-e29b-41d4-a716-446655440001', 'Sashka', '32c6b1625a1aae8ba1cbdbb24c20b624ed42ab7389c54c6a5a53d59fff0f2b59', 'Іван Іванов', 'MANAGER', 'ivanov@warehouse.ua'),
('550e8400-e29b-41d4-a716-446655440002', 'Operator Petrenko', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'Петро Петренко', 'OPERATOR', 'petrenko@warehouse.ua');

INSERT INTO Warehouses (warehouse_id, name, address, capacity_sqm) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Центральний Склад Київ', 'вул. Промислова, 1, Київ', 5000.0),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Західний Філіал Львів', 'вул. Городоцька, 200, Львів', 2500.0);

INSERT INTO Zones (zone_id, warehouse_id, name, zone_type) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Зона А1 - Холодна', 'COLD'),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Зона А2 - Суха', 'DRY'),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Зона Л1 - Загальна', 'GENERAL'),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Зона Небезпечних Вантажів', 'HAZARDOUS');

INSERT INTO ProductCategories (category_id, category_name, description) VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 'Електроніка', 'Побутова та комп''ютерна техніка'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 'Продукти', 'Харчові продукти тривалого зберігання'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a43', 'Заморозка', 'Продукти, що потребують низьких температур'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'Хімія', 'Побутова хімія та небезпечні речовини'),
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 'Офіс', 'Канцелярія та меблі');

INSERT INTO Suppliers (supplier_id, name, contact_person, email, phone, address) VALUES
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'ТехноСвіт ТОВ', 'Олексій', 'info@technosvit.ua', '+380441234567', 'Київ, пр-т Перемоги, 45'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a52', 'ЕкоПродукт ПрАТ', 'Марія', 'sales@ecoproduct.com', '+380447654321', 'Житомир, вул. Поліська, 12'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a53', 'ХімАльянс', 'Сергій', 'support@him.ua', '+380671112233', 'Одеса, вул. Хіміків, 3'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a54', 'Молочний Дім', 'Олена', 'milk@md.ua', '+380509998877', 'Полтава, вул. Заводська, 8'),
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'ОфісЦентр', 'Андрій', 'order@office.ua', '+380635554433', 'Київ, вул. Велика Васильківська, 100');

INSERT INTO Products (product_id, category_id, supplier_id, sku, name, description, unit, price, min_stock_level) VALUES
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a61', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'EL-LAP-001', 'Ноутбук Dell XPS 13', 'Потужний ноутбук для роботи', 'pcs', 45000.0, 5),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a62', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'EL-MON-002', 'Монітор LG 27"', '4K монітор для професіоналів', 'pcs', 12000.0, 10),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a63', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a52', 'PR-PAS-001', 'Макаронні вироби 500г', 'Тверді сорти пшениці', 'box', 35.0, 100),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a64', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a43', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a54', 'FR-ICE-001', 'Морозиво "Пломбір"', 'Класичне вершкове', 'pcs', 25.0, 200),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a65', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a53', 'CH-CLN-001', 'Миючий засіб 1л', 'Універсальний засіб', 'bottle', 85.0, 50),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'EL-MOU-003', 'Мишка Logitech MX Master 3', 'Бездротова ергономічна мишка', 'pcs', 3500.0, 20),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a67', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a52', 'PR-TEA-001', 'Чай чорний 100г', 'Цейлонський байховий', 'box', 60.0, 150),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a68', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'OF-PAP-001', 'Папір А4 500л', 'Щільність 80г/м2', 'ream', 180.0, 300),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a69', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a43', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a54', 'FR-VEG-001', 'Суміш овочева 400г', 'Швидка заморозка', 'pcs', 45.0, 120),
('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a70', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a53', 'CH-SOAP-001', 'Мило рідке 5л', 'Антибактеріальне', 'canister', 250.0, 40);

INSERT INTO Inventory (inventory_id, product_id, location_id, quantity) VALUES
('01eebc99-9c0b-4ef8-bb6d-6bb9bd380a81', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a61', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 15),
('01eebc99-9c0b-4ef8-bb6d-6bb9bd380a82', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a62', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 25),
('01eebc99-9c0b-4ef8-bb6d-6bb9bd380a83', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a63', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a36', 500),
('01eebc99-9c0b-4ef8-bb6d-6bb9bd380a84', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a64', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 300),
('01eebc99-9c0b-4ef8-bb6d-6bb9bd380a85', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a65', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a38', 120),
('01eebc99-9c0b-4ef8-bb6d-6bb9bd380a86', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a35', 40),
('01eebc99-9c0b-4ef8-bb6d-6bb9bd380a87', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a67', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a37', 200),
('01eebc99-9c0b-4ef8-bb6d-6bb9bd380a88', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a68', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a36', 450);

INSERT INTO Orders (order_id, order_number, type, status, created_at, required_date, notes) VALUES
('12eebc99-9c0b-4ef8-bb6d-6bb9bd380a91', 'ORD-2024-001', 'INBOUND', 'DELIVERED', '2024-04-01 10:00:00', '2024-04-05 18:00:00', 'Постачання техніки'),
('12eebc99-9c0b-4ef8-bb6d-6bb9bd380a92', 'ORD-2024-002', 'OUTBOUND', 'SHIPPED', '2024-04-10 14:30:00', '2024-04-15 12:00:00', 'Замовлення клієнта А'),
('12eebc99-9c0b-4ef8-bb6d-6bb9bd380a93', 'ORD-2024-003', 'INBOUND', 'PENDING', '2024-04-28 09:15:00', '2024-05-02 18:00:00', 'Очікуємо морозиво'),
('12eebc99-9c0b-4ef8-bb6d-6bb9bd380a94', 'ORD-2024-004', 'OUTBOUND', 'PROCESSING', '2024-04-29 11:00:00', '2024-04-30 17:00:00', 'Термінова відправка');

INSERT INTO OrderItems (order_item_id, order_id, product_id, quantity, unit_price) VALUES
('23eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', '12eebc99-9c0b-4ef8-bb6d-6bb9bd380a91', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a61', 5, 44500.0),
('23eebc99-9c0b-4ef8-bb6d-6bb9bd380b02', '12eebc99-9c0b-4ef8-bb6d-6bb9bd380a91', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a62', 10, 11800.0),
('23eebc99-9c0b-4ef8-bb6d-6bb9bd380b03', '12eebc99-9c0b-4ef8-bb6d-6bb9bd380a92', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a61', 2, 45000.0),
('23eebc99-9c0b-4ef8-bb6d-6bb9bd380b04', '12eebc99-9c0b-4ef8-bb6d-6bb9bd380a92', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 3, 3500.0),
('23eebc99-9c0b-4ef8-bb6d-6bb9bd380b05', '12eebc99-9c0b-4ef8-bb6d-6bb9bd380a93', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a64', 100, 22.0),
('23eebc99-9c0b-4ef8-bb6d-6bb9bd380b06', '12eebc99-9c0b-4ef8-bb6d-6bb9bd380a94', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a68', 50, 180.0),
('23eebc99-9c0b-4ef8-bb6d-6bb9bd380b07', '12eebc99-9c0b-4ef8-bb6d-6bb9bd380a94', 'f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a65', 10, 85.0);
