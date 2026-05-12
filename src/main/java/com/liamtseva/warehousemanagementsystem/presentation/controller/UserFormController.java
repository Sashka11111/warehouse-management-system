package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.UserRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.UserRepositoryImpl;
import java.time.LocalDateTime;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserFormController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private VBox passwordContainer;
    @FXML private ComboBox<UserRole> roleComboBox;
    @FXML private Label formTitleLabel;
    @FXML private Label formSubtitleLabel;
    @FXML private Button saveButton;

    private Stage stage;
    private final UserRepository repository;
    private User userToEdit;
    private Runnable onSaveCallback;

    public UserFormController() {
        this.repository = new UserRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUserToEdit(User user) {
        this.userToEdit = user;
        if (user != null) {
            formTitleLabel.setText("Редагувати користувача");
            formSubtitleLabel.setText("Оновіть права та персональні дані");
            usernameField.setText(user.username());
            emailField.setText(user.email());
            roleComboBox.setValue(user.role());
            passwordField.setText("********");
            passwordField.setDisable(true);
            saveButton.setText("Оновити користувача");
        } else {
            formTitleLabel.setText("Додати користувача");
            formSubtitleLabel.setText("Налаштування облікового запису та прав доступу");
            usernameField.clear();
            emailField.clear();
            passwordField.clear();
            passwordField.setDisable(false);
            roleComboBox.setValue(null);
            saveButton.setText("Зберегти дані");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        try {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            UserRole role = roleComboBox.getValue();

            if (username.isEmpty() || email.isEmpty() || role == null) {
                AlertController.showAlert("Всі поля, крім пароля при редагуванні, обов'язкові");
                return;
            }

            if (userToEdit == null) {
                if (password.isEmpty()) {
                    AlertController.showAlert("Пароль обов'язковий для нового користувача");
                    return;
                }
                User newUser = new User(UUID.randomUUID(), username, password, role, email, LocalDateTime.now());
                repository.create(newUser);
                AlertController.showInfo("Користувача додано");
            } else {
                User updated = new User(userToEdit.userId(), username, userToEdit.password(), role, email, userToEdit.createdAt());
                repository.update(updated);
                AlertController.showInfo("Дані оновлено");
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
