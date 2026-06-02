package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.AuthenticatedUser;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainMenuController {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button productsButton;
    @FXML
    private Button suppliersButton;
    @FXML
    private Button inventoryButton;
    @FXML
    private Button zonesButton;
    @FXML
    private Button categoriesButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button changeAccountButton;

    @FXML
    private Label userName;


    @FXML
    private StackPane contentArea;
    @FXML
    private StackPane stackPane;

    @FXML
    void initialize() {
        showDashboardPage();

        dashboardButton.setOnAction(event -> showDashboardPage());
        zonesButton.setOnAction(event -> showZonesPage());
        productsButton.setOnAction(event -> showProductsPage());
        categoriesButton.setOnAction(event -> showCategoriesPage());
        suppliersButton.setOnAction(event -> showSuppliersPage());
        inventoryButton.setOnAction(event -> showInventoryPage());
        usersButton.setOnAction(event -> showUsersPage());
        changeAccountButton.setOnAction(event -> handleChangeAccountAction());

        User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
        if (currentUser != null) {
            userName.setText(currentUser.username());
            applyRolePermissions(currentUser.role());
        }

        Platform.runLater(() -> {
            if (contentArea.getScene() != null) {
                Stage primaryStage = (Stage) contentArea.getScene().getWindow();
                
                primaryStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
                    adjustSidebar(newVal.doubleValue());
                });
                adjustSidebar(primaryStage.getScene().getWidth());
            }
        });
    }

    private void adjustSidebar(double width) {
        AnchorPane root = (AnchorPane) contentArea.getScene().getRoot();
        AnchorPane sidebar = (AnchorPane) root.getChildren().get(0);
        
        if (width < 1000) {
            sidebar.setPrefWidth(80);
            collapseButton(dashboardButton);
            collapseButton(zonesButton);
            collapseButton(productsButton);
            collapseButton(categoriesButton);
            collapseButton(suppliersButton);
            collapseButton(inventoryButton);
            collapseButton(usersButton);
            collapseButton(changeAccountButton);
            
            AnchorPane.setLeftAnchor(root.getChildren().get(2), 80.0);
        } else {
            sidebar.setPrefWidth(240);
            expandButton(dashboardButton, "Головна");
            expandButton(zonesButton, "Зони");
            expandButton(productsButton, "Товари");
            expandButton(categoriesButton, "Категорії");
            expandButton(suppliersButton, "Постачальники");
            expandButton(inventoryButton, "Складські залишки");
            expandButton(usersButton, "Користувачі");
            expandButton(changeAccountButton, "Вихід");
            
            AnchorPane.setLeftAnchor(root.getChildren().get(2), 240.0);
        }
    }

    private void collapseButton(Button btn) {
        btn.setText("");
        btn.setPrefWidth(60);
    }

    private void expandButton(Button btn, String text) {
        btn.setText(text);
        btn.setPrefWidth(220);
    }

    private void applyRolePermissions(UserRole role) {
        if (role == UserRole.ADMIN) return;

        if (role == UserRole.MANAGER) {
            hideMenuButton(usersButton);
        }

        if (role == UserRole.OPERATOR) {
            hideMenuButton(usersButton);
            hideMenuButton(categoriesButton);
            hideMenuButton(suppliersButton);
            hideMenuButton(zonesButton);
        }
    }

    private void hideMenuButton(Button button) {
        button.setVisible(false);
        button.setManaged(false);
    }

    private void moveStackPaneIndicator(Button button) {
        double buttonY = button.getBoundsInParent().getMinY();
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), stackPane);
        transition.setToY(buttonY);
        transition.play();
    }

    private void showDashboardPage() {
        moveStackPaneIndicator(dashboardButton);
        loadFXML("/view/dashboard.fxml");
    }

    private void showZonesPage() {
        moveStackPaneIndicator(zonesButton);
        loadFXML("/view/zones.fxml");
    }


    private void showProductsPage() {
        moveStackPaneIndicator(productsButton);
        loadFXML("/view/products.fxml");
    }

    private void showCategoriesPage() {
        moveStackPaneIndicator(categoriesButton);
        loadFXML("/view/categories.fxml");
    }

    private void showSuppliersPage() {
        moveStackPaneIndicator(suppliersButton);
        loadFXML("/view/suppliers.fxml");
    }

    private void showInventoryPage() {
        moveStackPaneIndicator(inventoryButton);
        loadFXML("/view/inventory.fxml");
    }



    private void showUsersPage() {
        moveStackPaneIndicator(usersButton);
        loadFXML("/view/users.fxml");
    }

    private void loadFXML(String fxmlFileName) {
        try {
            java.net.URL fxmlUrl = getClass().getResource(fxmlFileName);
            if (fxmlUrl == null) {
                throw new IOException("FXML file not found: " + fxmlFileName);
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent fxml = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxml);
        } catch (Exception ex) {
            Logger.getLogger(MainMenuController.class.getName()).log(Level.WARNING, "Cannot load FXML: " + fxmlFileName, ex);
            contentArea.getChildren().clear();
            Label placeholder = new Label("Сторінка " + fxmlFileName + " ще не створена");
            placeholder.setStyle("-fx-font-size: 18px; -fx-text-fill: #2f3479;");
            contentArea.getChildren().add(placeholder);
        }
    }

    private void handleChangeAccountAction() {
        try {
            Stage currentStage = (Stage) changeAccountButton.getScene().getWindow();
            boolean isMaximized = currentStage.isMaximized();
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"));
                    Parent root = loader.load();

                    Stage loginStage = new Stage();
                    try {
                        loginStage.getIcons().add(new Image(getClass().getResourceAsStream("/data/icon.png")));
                    } catch (Exception ignored) {}
                    
                    loginStage.initStyle(StageStyle.UNDECORATED);
                    if (isMaximized) {
                        loginStage.setMaximized(true);
                    }
                    Scene scene = new Scene(root, 1000, 690);
                    scene.getRoot().setOpacity(0.0);
                    loginStage.setScene(scene);
                    loginStage.show();

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), scene.getRoot());
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();

                    currentStage.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            fadeOut.play();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
