package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.UserRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.UserRepositoryImpl;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UserController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> roleColumn;

    private final UserRepository repository;
    private final ObservableList<User> userData = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;
    private User selectedUser;

    public UserController() {
        this.repository = new UserRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        filteredData = new FilteredList<>(userData, p -> true);
        setupTable();
        loadUsers();
        setupListeners();
    }

    private void setupTable() {
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().username()));
        fullNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().email()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().role().toString()));
        
        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedData);
        
        Label placeholder = new Label("Нічого не знайдено за вашим запитом");
        placeholder.getStyleClass().add("table-placeholder");
        userTable.setPlaceholder(placeholder);
    }
    private void loadUsers() {
        List<User> users = repository.findAll();
        userData.setAll(users);
        
        ObservableList<String> roles = FXCollections.observableArrayList("Всі ролі");
        for (var role : com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole.values()) {
            roles.add(role.toString());
        }
        roleFilter.setItems(roles);
        roleFilter.getSelectionModel().selectFirst();
    }

    private void setupListeners() {
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedUser = newSelection;
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> applyFilters());
        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String selectedRole = roleFilter.getValue();
        
        filteredData.setPredicate(user -> {
            boolean matchesText = searchText.isEmpty() ||
                user.username().toLowerCase().contains(searchText) ||
                user.email().toLowerCase().contains(searchText);

            boolean matchesRole = selectedRole == null ||
                                 selectedRole.equals("Всі ролі") ||
                                 user.role().toString().equals(selectedRole);

            return matchesText && matchesRole;
        });
    }


    @FXML
    private void openAddDialog() {
        showFormDialog(null);
    }

    @FXML
    private void openEditDialog() {
        if (selectedUser == null) {
            AlertController.showAlert("Будь ласка, виберіть користувача для редагування");
            return;
        }
        showFormDialog(selectedUser);
    }

    private void showFormDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user_form.fxml"));
            VBox root = loader.load();
            UserFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);

            controller.setStage(stage);
            controller.setUserToEdit(user);
            controller.setOnSaveCallback(this::loadUsers);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка завантаження форми: " + e.toString());
        }
    }

    @FXML
    private void deleteUser() {
        if (selectedUser == null) {
            AlertController.showAlert("Будь ласка, виберіть користувача для видалення");
            return;
        }

        try {
            repository.deleteById(selectedUser.userId());
            loadUsers();
            AlertController.showInfo("Користувача видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }
}
