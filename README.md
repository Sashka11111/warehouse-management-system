# 📦 Warehouse Management System (WMS)

[![Java Version](https://img.shields.io/badge/Java-25-orange.svg?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-25-blue.svg?style=for-the-badge)](https://openjfx.io/)
[![AtlantaFX Theme](https://img.shields.io/badge/Theme-AtlantaFX_Primer-violet.svg?style=for-the-badge)](https://mkpaz.github.io/atlantafx/)
[![Database](https://img.shields.io/badge/Database-SQLite-lightgrey.svg?style=for-the-badge&logo=sqlite)](https://sqlite.org/)
[![Build Tool](https://img.shields.io/badge/Build-Maven-red.svg?style=for-the-badge&logo=apachemaven)](https://maven.apache.org/)

**Система управління складом (WMS)** — це сучасний настільний застосунок (desktop application), розроблений на мові Java 25 з використанням JavaFX. Проєкт спроєктований із застосуванням сучасних паттернів проектування та архітектурних рішень, що гарантує швидку та надійну роботу з базами даних, гнучку фільтрацію даних і сучасний естетичний вигляд (завдяки стилям **AtlantaFX**).

---

## ✨ Основні можливості (Features)

*   📊 **Модернізований Dashboard (Головна панель):**
    *   Візуалізація ключових метрик: загальна вартість товарів, кількість товарних позицій на складі, статус заповненості зон.
    *   Швидкий доступ до останніх оновлень інвентарю.
*   🗺️ **Управління зонами зберігання (Zone Management):**
    *   Групування товарів за температурними та фізичними зонами: `COLD` (холодна), `DRY` (суха), `HAZARDOUS` (небезпечні речовини), `GENERAL` (загального призначення).
*   📦 **Повний цикл обліку товарів:**
    *   Створення, редагування, категоризація та контроль SKU.
    *   Відстеження критичного рівня залишків (`min_stock_level`) для запобігання дефіциту товарів.
*   🔍 **Професійна фільтрація та пошук:**
    *   Миттєвий пошук товарів та постачальників за ключовими словами. 
    *   Aільтри за категоріями та зонами зберігання.
*   🔒 **Безпека та Ролі:**
    *   Хешування паролів за допомогою алгоритму SHA-256.
    *   Рольова модель доступу (Рівні: `ADMIN` / `MANAGER` / `OPERATOR`).
*   🎨 **Сучасний UX/UI:**
    *   Дизайн на базі теми **AtlantaFX Primer**.
    *   Усі іконки підвантажуються через сучасний пакет **Ikonli (FontAwesome 5)**.
    *   Кастомний безрамковий інтерфейс (Undecorated Stage).

---

## 📂 Структура проєкту

Проєкт реалізовано за трирівневою класичною архітектурною схемою (Layered Architecture):

```
com.liamtseva.warehousemanagementsystem
│
├── Main.java                        # Точка старту (запуск JVM)
├── WarehouseApp.java                # Головний клас ініціалізації JavaFX
│
├── domain                           # Доменна логіка та бізнес-правила
│   ├── exception/                   # Кастомні винятки (напр. EntityNotFoundException)
│   └── security/                    # Хешування паролів та контекст автентифікації
│
├── persistence                      # Шар доступу до даних (Data Access Layer)
│   ├── connection/                  # Пул з'єднань HikariCP та ініціалізація БД
│   ├── entity/                      # Сутності бази даних (Product, Zone, User, etc.)
│   └── repository/                  # Репозиторії (інтерфейси та SQLite-реалізації)
│
└── presentation                     # Шар представлення (UI та контролери)
    ├── controller/                  # Контролери FXML вікон (Dashboard, Inventory, etc.)
    └── validation/                  # Валідатори для форм вводу даних
```

---

## 🗄 Структура Бази Даних

При першому запуску застосунок створює локальний файл бази даних у домашній директорії користувача:
`~/.warehouse-management-system/warehouse.sqlite`

---

## 🚀 Встановлення та запуск

### 💻 Для розробників (Збірка з коду)

Вам знадобляться:
*   **JDK 25** або новішої версії.
*   **Maven 3.9+**.

1.  **Клонуйте репозиторій:**
    ```bash
    git clone https://github.com/Sashka11111/warehouse-management-system.git
    cd warehouse-management-system
    ```

2.  **Запуск у режимі розробки:**
    запустіть застосунок безпосередньо з вихідного коду:
    ```bash
    mvn clean javafx:run
    ```

3.  **Збірка готового JAR-файлу (Fat JAR):**
    для створення автономного JAR-архіву, що містить усі необхідні залежності:
    ```bash
    mvn clean package -DskipTests
    ```
    Після завершення збірки файл з'явиться у папці `target`. Запустити його можна так:
    ```bash
    java -jar target/warehouse-management-system-1.0-SNAPSHOT.jar
    ```

---

## 👥 Рольова модель та безпека

1.  **ADMIN:** Повний доступ до всіх розділів, включаючи керування користувачами та системними налаштуваннями.
2.  **MANAGER:** Керування товарами, зонами, постачальниками та інвентаризацією. Без права доступу до розділу користувачів.
3.  **OPERATOR:** Перегляд інвентарю, додавання та списання товарів у межах наявних зон.

---

## 📝 Ліцензія та контакти

Розроблено в рамках навчальної курсової роботи.
*   **Ліцензія:** [MIT License](LICENSE)
*   **Автор:** [Лямцева Олександра](https://github.com/Sashka11111)

