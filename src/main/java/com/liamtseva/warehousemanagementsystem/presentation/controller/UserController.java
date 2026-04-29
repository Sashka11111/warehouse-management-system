package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.UserRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.UserRepositoryImpl;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UserController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<UserRole> roleComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> fullNameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, String> dateColumn;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final UserRepository userRepository;
    private ObservableList<User> userList;
    private User selectedUser;

    public UserController() {
        this.userRepository = new UserRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().username()));
        fullNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().fullName()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().email()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().role().toString()));
        dateColumn.setCellValueFactory(data -> {
            if (data.getValue().createdAt() != null) {
                return new SimpleStringProperty(data.getValue().createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            return new SimpleStringProperty("");
        });

        loadUsers();

        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values()));

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchUsers(newVal));

        editButton.setOnAction(event -> editUser());
        deleteButton.setOnAction(event -> deleteUser());
        clearButton.setOnAction(event -> clearFields());

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedUser = newVal;
            if (newVal != null) {
                usernameField.setText(newVal.username());
                fullNameField.setText(newVal.fullName());
                emailField.setText(newVal.email());
                roleComboBox.setValue(newVal.role());
            }
        });
    }

    private void loadUsers() {
        userList = FXCollections.observableArrayList(userRepository.findAll());
        userTable.setItems(userList);
    }

    private void searchUsers(String text) {
        if (text == null || text.isEmpty()) {
            userTable.setItems(userList);
            return;
        }
        String lowerText = text.toLowerCase();
        List<User> filtered = userList.stream()
                .filter(u -> u.username().toLowerCase().contains(lowerText) || u.fullName().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
        userTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void editUser() {
        if (selectedUser == null) {
            AlertController.showAlert("Виберіть користувача");
            return;
        }
        try {
            User updated = new User(
                selectedUser.userId(),
                usernameField.getText().trim(),
                selectedUser.password(), // Пароль не змінюємо тут
                fullNameField.getText().trim(),
                roleComboBox.getValue(),
                emailField.getText().trim(),
                selectedUser.createdAt()
            );
            userRepository.update(updated);
            loadUsers();
            clearFields();
            AlertController.showInfo("Дані користувача оновлено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }

    private void deleteUser() {
        if (selectedUser == null) {
            AlertController.showAlert("Виберіть користувача");
            return;
        }
        try {
            userRepository.deleteById(selectedUser.userId());
            loadUsers();
            clearFields();
            AlertController.showInfo("Користувача видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }

    private void clearFields() {
        usernameField.clear();
        fullNameField.clear();
        emailField.clear();
        roleComboBox.setValue(null);
        selectedUser = null;
        userTable.getSelectionModel().clearSelection();
    }
}
