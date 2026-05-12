package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.InventoryItem;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.InventoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ZoneRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.InventoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ZoneRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.presentation.validation.InventoryValidator;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ValidationResult;
import java.time.LocalDateTime;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class InventoryFormController {

    @FXML private ComboBox<Product> productComboBox;
    @FXML private ComboBox<Zone> zoneComboBox;
    @FXML private TextField quantityField;
    @FXML private Label formTitleLabel;
    @FXML private Label formSubtitleLabel;
    @FXML private Button saveButton;

    private Stage stage;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;
    
    private InventoryItem inventoryToEdit;
    private Runnable onSaveCallback;

    public InventoryFormController() {
        this.inventoryRepository = new InventoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.productRepository = new ProductRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.zoneRepository = new ZoneRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        loadComboData();
    }

    private void setupComboBoxes() {
        productComboBox.setConverter(new StringConverter<>() {
            @Override 
            public String toString(Product object) { 
                if (object == null) return "";
                String sku = object.sku() != null ? object.sku() : "N/A";
                String name = object.name() != null ? object.name() : "Невідомо";
                return sku + " - " + name; 
            }
            @Override public Product fromString(String string) { return null; }
        });
        zoneComboBox.setConverter(new StringConverter<>() {
            @Override 
            public String toString(Zone object) { 
                if (object == null) return "";
                return object.name() != null ? object.name() : "Невідома зона";
            }
            @Override public Zone fromString(String string) { return null; }
        });
    }

    private void loadComboData() {
        productComboBox.setItems(FXCollections.observableArrayList(productRepository.findAll()));
        zoneComboBox.setItems(FXCollections.observableArrayList(zoneRepository.findAll()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInventoryToEdit(InventoryItem inventory) {
        this.inventoryToEdit = inventory;
        if (inventory != null) {
            formTitleLabel.setText("Редагувати залишки");
            formSubtitleLabel.setText("Оновіть кількість або місце зберігання");
            quantityField.setText(String.valueOf(inventory.quantity()));
            
            productComboBox.getItems().stream()
                .filter(p -> p.productId().equals(inventory.productId()))
                .findFirst().ifPresent(productComboBox::setValue);
            
            zoneComboBox.getItems().stream()
                .filter(z -> z.zoneId().equals(inventory.zoneId()))
                .findFirst().ifPresent(zoneComboBox::setValue);

            saveButton.setText("Оновити дані");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        try {
            Product product = productComboBox.getValue();
            Zone zone = zoneComboBox.getValue();
            String quantityStr = quantityField.getText().trim();

            Integer quantity = null;
            if (!quantityStr.isEmpty()) {
                try {
                    quantity = Integer.parseInt(quantityStr);
                } catch (NumberFormatException e) {
                    AlertController.showAlert("Кількість повинна бути цілим числом");
                    return;
                }
            }

            InventoryItem validationItem = new InventoryItem(
                    inventoryToEdit == null ? null : inventoryToEdit.inventoryId(),
                    product == null ? null : product.productId(),
                    zone == null ? null : zone.zoneId(),
                    quantity,
                    LocalDateTime.now()
            );

            ValidationResult result = InventoryValidator.isInventoryValid(validationItem);
            if (!result.isValid()) {
                AlertController.showAlert(String.join("\n", result.getErrors()));
                return;
            }

            if (inventoryToEdit == null) {
                InventoryItem newInventory = new InventoryItem(UUID.randomUUID(), product.productId(), zone.zoneId(), quantity, LocalDateTime.now());
                inventoryRepository.create(newInventory);
                AlertController.showInfo("Товар розміщено");
            } else {
                InventoryItem updated = new InventoryItem(inventoryToEdit.inventoryId(), product.productId(), zone.zoneId(), quantity, LocalDateTime.now());
                inventoryRepository.update(updated);
                AlertController.showInfo("Залишки оновлено");
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
