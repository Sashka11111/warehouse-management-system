INSERT INTO Users (user_id, username, password, role, email) VALUES
    ('a3f1e2d4-5b6c-7d8e-9f0a-1b2c3d4e5f60', 'Admin', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'ADMIN', 'admin@atb-market.com'),
    ('b4e2f3c5-6d7e-8f9a-0b1c-2d3e4f5a6b71', 'Sashka', '32c6b1625a1aae8ba1cbdbb24c20b624ed42ab7389c54c6a5a53d59fff0f2b59', 'MANAGER', 'sashka@atb-market.com'),
    ('c5d3a4b6-7e8f-9a0b-1c2d-3e4f5a6b7c82', 'Operator', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'OPERATOR', 'operator1@atb-market.com');

INSERT INTO Zones (zone_id, name, zone_type) VALUES
    ('d6e4b5c7-8f9a-0b1c-2d3e-4f5a6b7c8d93', 'Холодильна камера (М''ясо/Молоко)', 'COLD'),
    ('e7f5c6d8-9a0b-1c2d-3e4f-5a6b7c8d9ea4', 'Сухий склад (Бакалія/Снеки)', 'DRY'),
    ('f8a6d7e9-0b1c-2d3e-4f5a-6b7c8d9eafb5', 'Зона овочів та фруктів', 'GENERAL'),
    ('09b7e8fa-1c2d-3e4f-5a6b-7c8d9eafb0c6', 'Відділ алкогольних напоїв', 'GENERAL'),
    ('1ac8f9ab-2d3e-4f5a-6b7c-8d9eafb0c1d7', 'Побутова хімія', 'GENERAL');

INSERT INTO ProductCategories (category_id, category_name, description) VALUES
    ('2bd90abc-3e4f-5a6b-7c8d-9eafb0c1d2e8', 'Молочні продукти', 'Молоко, сир, йогурти'),
    ('3ce0abcd-4f5a-6b7c-8d9e-afb0c1d2e3f9', 'М''ясні вироби', 'Ковбаси, свіже м''ясо, птиця'),
    ('4df1bcde-5a6b-7c8d-9eaf-b0c1d2e3f4a0', 'Хлібобулочні вироби', 'Хліб, булки, кондитерка'),
    ('5ea2cdef-6b7c-8d9e-afb0-c1d2e3f4a5b1', 'Бакалія', 'Крупи, макарони, олія, цукор'),
    ('6fb3def0-7c8d-9eaf-b0c1-d2e3f4a5b6c2', 'Напої', 'Вода, соки, газовані напої'),
    ('70c4ef01-8d9e-afb0-c1d2-e3f4a5b6c7d3', 'Алкоголь', 'Пиво, вино, міцні напої'),
    ('81d5f012-9eaf-b0c1-d2e3-f4a5b6c7d8e4', 'Овочі та фрукти', 'Свіжі овочі, фрукти, зелень'),
    ('92e60123-afb0-c1d2-e3f4-a5b6c7d8e9f5', 'Побутова хімія', 'Миючі засоби, порошки, гігієна');

INSERT INTO Suppliers (supplier_id, name, contact_person, email, phone, address) VALUES
    ('a3f71234-b0c1-d2e3-f4a5-b6c7d8e9f0a6', 'Молочний Альянс', 'Іван Морозенко', 'sales@molo.ua', '+380441234567', 'м. Київ, вул. Молочна, 10'),
    ('b4082345-c1d2-e3f4-a5b6-c7d8e9f0a1b7', 'М''ясна Гільдія', 'Петро Ковбасюк', 'meat@guild.ua', '+380509876543', 'м. Житомир, вул. М''ясна, 5'),
    ('c5193456-d2e3-f4a5-b6c7-d8e9f0a1b2c8', 'Roshen', 'Олена Солодка', 'office@roshen.com', '+380445556677', 'м. Київ, пр-т Науки, 1'),
    ('d62a4567-e3f4-a5b6-c7d8-e9f0a1b2c3d9', 'Sandora (PepsiCo)', 'Дмитро Сік', 'info@sandora.ua', '+380512334455', 'Миколаївська обл., с. Миколаївське'),
    ('e73b5678-f4a5-b6c7-d8e9-f0a1b2c3d4ea', 'Danone (Весела Ферма)', 'Анна Данон', 'contact@danone.ua', '+380442223344', 'м. Херсон, вул. Заводська, 15');

INSERT INTO Products (product_id, category_id, supplier_id, sku, name, description, unit, price, min_stock_level) VALUES ('f84c6789-a5b6-c7d8-e9f0-a1b2c3d4e5fb', '2bd90abc-3e4f-5a6b-7c8d-9eafb0c1d2e8', 'e73b5678-f4a5-b6c7-d8e9-f0a1b2c3d4ea', 'SKU-001', 'Молоко "Весела Ферма" 2.5%', 'Пакет 900г', 'шт', 35.50, 50),
    ('095d789a-b6c7-d8e9-f0a1-b2c3d4e5f60c', '2bd90abc-3e4f-5a6b-7c8d-9eafb0c1d2e8', 'a3f71234-b0c1-d2e3-f4a5-b6c7d8e9f0a6', 'SKU-002', 'Сир "Пирятин" Класичний', 'Брусок 180г', 'шт', 55.00, 30),
    ('1a6e89ab-c7d8-e9f0-a1b2-c3d4e5f6071d', '2bd90abc-3e4f-5a6b-7c8d-9eafb0c1d2e8', 'a3f71234-b0c1-d2e3-f4a5-b6c7d8e9f0a6', 'SKU-003', 'Масло "Яготинське" 73%', 'Пачка 200г', 'шт', 68.00, 40),
    ('2b7f9abc-d8e9-f0a1-b2c3-d4e5f607182e', '3ce0abcd-4f5a-6b7c-8d9e-afb0c1d2e3f9', 'b4082345-c1d2-e3f4-a5b6-c7d8e9f0a1b7', 'SKU-010', 'Ковбаса "Докторська" АТБ', 'Вищий сорт, кг', 'кг', 180.00, 20),
    ('3c80abcd-e9f0-a1b2-c3d4-e5f60718293f', '3ce0abcd-4f5a-6b7c-8d9e-afb0c1d2e3f9', 'b4082345-c1d2-e3f4-a5b6-c7d8e9f0a1b7', 'SKU-011', 'Філе куряче "Наша Ряба"', 'Охолоджене, кг', 'кг', 155.00, 100),
    ('4d91bcde-f0a1-b2c3-d4e5-f6071829304a', '5ea2cdef-6b7c-8d9e-afb0-c1d2e3f4a5b1', 'c5193456-d2e3-f4a5-b6c7-d8e9f0a1b2c8', 'SKU-020', 'Гречка "Розумний Вибір"', 'Пакет 1кг', 'шт', 42.00, 200),
    ('5ea2cdef-a1b2-c3d4-e5f6-071829304a5b', '5ea2cdef-6b7c-8d9e-afb0-c1d2e3f4a5b1', 'd62a4567-e3f4-a5b6-c7d8-e9f0a1b2c3d9', 'SKU-021', 'Олія "Олейна" Соняшникова', 'Пляшка 0.85л', 'шт', 58.00, 150),
    ('6fb3def0-b2c3-d4e5-f607-1829304a5b6c', '5ea2cdef-6b7c-8d9e-afb0-c1d2e3f4a5b1', 'c5193456-d2e3-f4a5-b6c7-d8e9f0a1b2c8', 'SKU-022', 'Макарони "Своя Лінія" Спіральки', 'Пакет 400г', 'шт', 22.50, 100),
    ('70c4ef01-c3d4-e5f6-0718-29304a5b6c7d', '6fb3def0-7c8d-9eaf-b0c1-d2e3f4a5b6c2', 'd62a4567-e3f4-a5b6-c7d8-e9f0a1b2c3d9', 'SKU-030', 'Вода "Моршинська" негазована', 'Пляшка 1.5л', 'шт', 18.50, 300),
    ('81d5f012-d4e5-f607-1829-304a5b6c7d8e', '6fb3def0-7c8d-9eaf-b0c1-d2e3f4a5b6c2', 'd62a4567-e3f4-a5b6-c7d8-e9f0a1b2c3d9', 'SKU-031', 'Сік "Сандора" Апельсин', 'Тетрапак 1л', 'шт', 48.00, 80),
    ('92e60123-e5f6-0718-2930-4a5b6c7d8e9f', '6fb3def0-7c8d-9eaf-b0c1-d2e3f4a5b6c2', 'd62a4567-e3f4-a5b6-c7d8-e9f0a1b2c3d9', 'SKU-032', 'Coca-Cola', 'Пляшка 2л', 'шт', 38.00, 120),
    ('a3f71234-f607-1829-304a-5b6c7d8e9fa0', '81d5f012-9eaf-b0c1-d2e3-f4a5b6c7d8e4', NULL, 'SKU-040', 'Яблуко Голден', 'Україна, кг', 'кг', 28.00, 150),
    ('b4082345-0718-2930-4a5b-6c7d8e9fa0b1', '81d5f012-9eaf-b0c1-d2e3-f4a5b6c7d8e4', NULL, 'SKU-041', 'Банан Еквадор', 'кг', 'кг', 62.00, 200),
    ('c5193456-1829-304a-5b6c-7d8e9fa0b1c2', '81d5f012-9eaf-b0c1-d2e3-f4a5b6c7d8e4', NULL, 'SKU-042', 'Картопля молода', 'кг', 'кг', 15.00, 500),
    ('d62a4567-2930-4a5b-6c7d-8e9fa0b1c2d3', '92e60123-afb0-c1d2-e3f4-a5b6c7d8e9f5', 'e73b5678-f4a5-b6c7-d8e9-f0a1b2c3d4ea', 'SKU-050', 'Мило "De la Mark"', 'Рідке, 500мл', 'шт', 45.00, 60),
    ('e73b5678-304a-5b6c-7d8e-9fa0b1c2d3e4', '92e60123-afb0-c1d2-e3f4-a5b6c7d8e9f5', 'e73b5678-f4a5-b6c7-d8e9-f0a1b2c3d4ea', 'SKU-051', 'Порошок "Persil" Color', 'Упаковка 3кг', 'шт', 320.00, 25);

INSERT INTO Inventory (inventory_id, product_id, zone_id, quantity) VALUES
    ('f84c6789-4a5b-6c7d-8e9f-a0b1c2d3e4f5', 'f84c6789-a5b6-c7d8-e9f0-a1b2c3d4e5fb', 'd6e4b5c7-8f9a-0b1c-2d3e-4f5a6b7c8d93', 120),
    ('095d789a-5b6c-7d8e-9fa0-b1c2d3e4f506', '095d789a-b6c7-d8e9-f0a1-b2c3d4e5f60c', 'd6e4b5c7-8f9a-0b1c-2d3e-4f5a6b7c8d93', 45),
    ('1a6e89ab-6c7d-8e9f-a0b1-c2d3e4f50617', '1a6e89ab-c7d8-e9f0-a1b2-c3d4e5f6071d', 'd6e4b5c7-8f9a-0b1c-2d3e-4f5a6b7c8d93', 80),
    ('2b7f9abc-7d8e-9fa0-b1c2-d3e4f5061728', '2b7f9abc-d8e9-f0a1-b2c3-d4e5f607182e', 'd6e4b5c7-8f9a-0b1c-2d3e-4f5a6b7c8d93', 15),
    ('3c80abcd-8e9f-a0b1-c2d3-e4f506172839', '3c80abcd-e9f0-a1b2-c3d4-e5f60718293f', 'd6e4b5c7-8f9a-0b1c-2d3e-4f5a6b7c8d93', 110),
    ('4d91bcde-9fa0-b1c2-d3e4-f50617283940', '4d91bcde-f0a1-b2c3-d4e5-f6071829304a', 'e7f5c6d8-9a0b-1c2d-3e4f-5a6b7c8d9ea4', 350),
    ('5ea2cdef-a0b1-c2d3-e4f5-061728394051', '5ea2cdef-a1b2-c3d4-e5f6-071829304a5b', 'e7f5c6d8-9a0b-1c2d-3e4f-5a6b7c8d9ea4', 180),
    ('6fb3def0-b1c2-d3e4-f506-172839405162', '6fb3def0-b2c3-d4e5-f607-1829304a5b6c', 'e7f5c6d8-9a0b-1c2d-3e4f-5a6b7c8d9ea4', 220),
    ('70c4ef01-c2d3-e4f5-0617-283940516273', '70c4ef01-c3d4-e5f6-0718-29304a5b6c7d', 'e7f5c6d8-9a0b-1c2d-3e4f-5a6b7c8d9ea4', 500),
    ('81d5f012-d3e4-f506-1728-394051627384', 'a3f71234-f607-1829-304a-5b6c7d8e9fa0', 'f8a6d7e9-0b1c-2d3e-4f5a-6b7c8d9eafb5', 200),
    ('92e60123-e4f5-0617-2839-405162738495', 'b4082345-0718-2930-4a5b-6c7d8e9fa0b1', 'f8a6d7e9-0b1c-2d3e-4f5a-6b7c8d9eafb5', 150),
    ('a3f71234-f506-1728-3940-5162738495a6', '92e60123-e5f6-0718-2930-4a5b6c7d8e9f', '09b7e8fa-1c2d-3e4f-5a6b-7c8d9eafb0c6', 100),
    ('b4082345-0617-2839-4051-62738495a6b7', 'e73b5678-304a-5b6c-7d8e-9fa0b1c2d3e4', '1ac8f9ab-2d3e-4f5a-6b7c-8d9eafb0c1d7', 40);
