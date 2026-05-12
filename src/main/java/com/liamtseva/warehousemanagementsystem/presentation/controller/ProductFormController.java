package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductCategoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.SupplierRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ProductValidator;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ValidationResult;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ProductFormController {

    @FXML private TextField skuField;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField unitField;
    @FXML private TextField minStockField;
    @FXML private ComboBox<ProductCategory> categoryComboBox;
    @FXML private ComboBox<Supplier> supplierComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private Label formTitleLabel;
    @FXML private Label formSubtitleLabel;
    @FXML private Button saveButton;

    private Stage stage;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private Product productToEdit;
    private Runnable onSaveCallback;

    public ProductFormController() {
        this.productRepository = new ProductRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.categoryRepository = new ProductCategoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.supplierRepository = new SupplierRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        loadData();
    }

    private void setupComboBoxes() {
        categoryComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(ProductCategory object) { return object == null ? "" : object.categoryName(); }
            @Override public ProductCategory fromString(String string) { return null; }
        });
        supplierComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Supplier object) { return object == null ? "" : object.name(); }
            @Override public Supplier fromString(String string) { return null; }
        });
    }

    private void loadData() {
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryRepository.findAll()));
        supplierComboBox.setItems(FXCollections.observableArrayList(supplierRepository.findAll()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProductToEdit(Product product) {
        this.productToEdit = product;
        if (product != null) {
            formTitleLabel.setText("Редагувати товар");
            formSubtitleLabel.setText("Оновіть специфікації товару");
            skuField.setText(product.sku());
            nameField.setText(product.name());
            priceField.setText(product.price().toString());
            unitField.setText(product.unit());
            minStockField.setText(String.valueOf(product.minStockLevel()));
            descriptionArea.setText(product.description());
            
            categoryComboBox.getItems().stream()
                .filter(c -> c.categoryId().equals(product.categoryId()))
                .findFirst().ifPresent(categoryComboBox::setValue);
            
            supplierComboBox.getItems().stream()
                .filter(s -> s.supplierId().equals(product.supplierId()))
                .findFirst().ifPresent(supplierComboBox::setValue);

            saveButton.setText("Оновити дані");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        try {
            String sku = skuField.getText().trim();
            String name = nameField.getText().trim();
            String priceStr = priceField.getText().trim();
            String unit = unitField.getText().trim();
            String minStockStr = minStockField.getText().trim();
            ProductCategory category = categoryComboBox.getValue();
            Supplier supplier = supplierComboBox.getValue();
            String description = descriptionArea.getText().trim();

            if (category == null || supplier == null) {
                AlertController.showAlert("Будь ласка, оберіть категорію та постачальника");
                return;
            }

            Double price;
            Integer minStock;
            try {
                price = Double.parseDouble(priceStr);
                minStock = Integer.parseInt(minStockStr);
            } catch (NumberFormatException e) {
                AlertController.showAlert("Некоректний формат чисел (ціна або запас)");
                return;
            }

            Product productData = new Product(
                    productToEdit == null ? null : productToEdit.productId(),
                    category.categoryId(), supplier.supplierId(), sku, name, description, unit, price, minStock
            );

            ValidationResult result = ProductValidator.isProductValid(productData, productRepository);
            if (!result.isValid()) {
                AlertController.showAlert(String.join("\n", result.getErrors()));
                return;
            }

            if (productToEdit == null) {
                Product newProduct = new Product(UUID.randomUUID(), category.categoryId(), supplier.supplierId(), sku, name, description, unit, price, minStock);
                productRepository.create(newProduct);
                AlertController.showInfo("Товар успішно додано");
            } else {
                Product updated = new Product(productToEdit.productId(), category.categoryId(), supplier.supplierId(), sku, name, description, unit, price, minStock);
                productRepository.update(updated);
                AlertController.showInfo("Товар оновлено");
            }

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            stage.close();
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }
    @FXML
    private void handleClose() {
        stage.close();
    }
}
