package com.liamtseva.warehousemanagementsystem.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TopBarController {

    @FXML
    private AnchorPane topBarRoot;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button maximizeButton;

    @FXML
    private Button closeButton;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    void initialize() {
        topBarRoot.setOnMousePressed(this::onMousePressed);
        topBarRoot.setOnMouseDragged(this::onMouseDragged);

        closeButton.setOnAction(event -> {
            Stage stage = getStage(event.getSource());
            if (stage != null) {
                stage.close();
            } else {
                System.exit(0);
            }
        });

        if (minimizeButton != null) {
            minimizeButton.setOnAction(event -> {
                Stage stage = getStage(event.getSource());
                if (stage != null) {
                    stage.setIconified(true);
                }
            });
        }

        if (maximizeButton != null) {
            maximizeButton.setOnAction(event -> {
                Stage stage = getStage(event.getSource());
                if (stage != null) {
                    stage.setMaximized(!stage.isMaximized());
                }
            });
        }
    }

    private void onMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void onMouseDragged(MouseEvent event) {
        Stage stage = getStage(topBarRoot);
        if (stage != null) {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    }

    private Stage getStage(Object source) {
        if (source instanceof Node) {
            return (Stage) ((Node) source).getScene().getWindow();
        }
        return null;
    }
}
