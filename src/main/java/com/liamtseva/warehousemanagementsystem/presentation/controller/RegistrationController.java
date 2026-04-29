package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.PasswordHashing;
import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.UserRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.UserRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.presentation.validation.UserValidator;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ValidationResult;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {

    @FXML
    private Button signInButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Button btnClose;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    private final UserRepository userRepository;

    public RegistrationController() {
        this.userRepository = new UserRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    void initialize() {
        btnClose.setOnAction(event -> System.exit(0));

        signInButton.setOnAction(event -> switchScene("/view/authorization.fxml"));

        signUpButton.setOnAction(event -> handleSignUp());
    }

    private void switchScene(String fxmlPath) {
        try {
            Scene currentScene = signUpButton.getScene();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            currentScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка завантаження вікна");
        }
    }

    private void handleSignUp() {
        String username = loginField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();

        // Повна валідація за допомогою нового валідатора
        ValidationResult usernameRes = UserValidator.isUsernameUnique(username, null, userRepository);
        if (!usernameRes.isValid()) {
            AlertController.showAlert(usernameRes.getErrorMessage());
            return;
        }

        ValidationResult passwordRes = UserValidator.isPasswordValid(password);
        if (!passwordRes.isValid()) {
            AlertController.showAlert(passwordRes.getErrorMessage());
            return;
        }

        ValidationResult emailRes = UserValidator.isEmailValid(email);
        if (!emailRes.isValid()) {
            AlertController.showAlert(emailRes.getErrorMessage());
            return;
        }

        try {
            String hashedPassword = PasswordHashing.getInstance().hashedPassword(password);
            User newUser = new User(
                UUID.randomUUID(),
                username,
                hashedPassword,
                "", // Повне ім'я (можна додати поле у форму, поки порожньо)
                UserRole.OPERATOR,
                email,
                LocalDateTime.now()
            );

            userRepository.create(newUser);
            AlertController.showInfo("Реєстрація успішна! Тепер ви можете увійти.");
            switchScene("/view/authorization.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка при створенні користувача: " + e.getMessage());
        }
    }
}
