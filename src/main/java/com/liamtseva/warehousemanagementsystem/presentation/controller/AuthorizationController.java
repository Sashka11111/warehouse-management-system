package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.AuthenticatedUser;
import com.liamtseva.warehousemanagementsystem.domain.security.PasswordHashing;
import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.UserRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.UserRepositoryImpl;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AuthorizationController {

    @FXML
    private Button authSignInButton;

    @FXML
    private Button authSingUpButton;

    @FXML
    private TextField loginTextField;

    @FXML
    private PasswordField passwordField;

    private final UserRepository userRepository;

    public AuthorizationController() {
        this.userRepository = new UserRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    void initialize() {


        // Перехід до реєстрації
        authSignInButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/registration.fxml"));
                Parent root = loader.load();
                Scene currentScene = authSignInButton.getScene();
                currentScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
                AlertController.showAlert("Помилка завантаження вікна реєстрації");
            }
        });

        // Спроба входу
        authSingUpButton.setOnAction(event -> {
            String loginText = loginTextField.getText().trim();
            String loginPassword = passwordField.getText().trim();

            if (!loginText.isEmpty() && !loginPassword.isEmpty()) {
                Optional<User> userOpt = userRepository.findByUsername(loginText);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    String hashedPassword = PasswordHashing.getInstance().hashedPassword(loginPassword);
                    if (user.password().equals(hashedPassword)) {
                        AuthenticatedUser.getInstance().setCurrentUser(user);
                        Stage currentStage = (Stage) authSingUpButton.getScene().getWindow();
                        boolean isMaximized = currentStage.isMaximized();
                        currentStage.hide();
                        loadMainMenu(isMaximized);
                    } else {
                        AlertController.showAlert("Неправильний логін або пароль");
                    }
                } else {
                    AlertController.showAlert("Неправильний логін або пароль");
                }
            } else {
                AlertController.showAlert("Будь ласка, введіть логін та пароль");
            }
        });
    }

    private void loadMainMenu(boolean isMaximized) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/mainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            // Спробуємо додати іконку, якщо вона є
            try {
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/data/icon.png")));
            } catch (Exception e) {
                // Ignore if icon not found
            }
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.UNDECORATED);
            if (isMaximized) {
                stage.setMaximized(true);
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка завантаження головного меню");
        }
    }
}
