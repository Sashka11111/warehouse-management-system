package com.liamtseva.warehousemanagementsystem.presentation.controller;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.liamtseva.warehousemanagementsystem.domain.themes.ThemeManager;
import javafx.fxml.FXML;
import javafx.application.Application;
import javafx.scene.control.*;

public class ThemesController {

    @FXML
    private RadioButton cupertinoDarkRadioButton;

    @FXML
    private RadioButton darkThemeRadioButton;

    @FXML
    private RadioButton draculaRadioButton;

    @FXML
    private RadioButton lightThemeRadioButton;

    @FXML
    private RadioButton nordDarkThemeRadioButton;

    @FXML
    private RadioButton nordLightRadioButton;

    private String theme;
    private ToggleGroup themeToggleGroup;

    @FXML
    void initialize() {
        theme = ThemeManager.getCurrentTheme() != null ?
            ThemeManager.getCurrentTheme() :
            new PrimerLight().getUserAgentStylesheet();
        setInitialTheme();
        setupThemeToggleGroup();

        themeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> applyTheme());
    }

    private void setInitialTheme() {
        if (theme.equals(new PrimerDark().getUserAgentStylesheet())) {
            darkThemeRadioButton.setSelected(true);
        } else if (theme.equals(new PrimerLight().getUserAgentStylesheet())) {
            lightThemeRadioButton.setSelected(true);
        } else if (theme.equals(new NordLight().getUserAgentStylesheet())) {
            nordLightRadioButton.setSelected(true);
        } else if (theme.equals(new NordDark().getUserAgentStylesheet())) {
            nordDarkThemeRadioButton.setSelected(true);
        } else if (theme.equals(new CupertinoDark().getUserAgentStylesheet())) {
            cupertinoDarkRadioButton.setSelected(true);
        } else if (theme.equals(new Dracula().getUserAgentStylesheet())) {
            draculaRadioButton.setSelected(true);
        }
    }

    private void setupThemeToggleGroup() {
        themeToggleGroup = new ToggleGroup();
        lightThemeRadioButton.setToggleGroup(themeToggleGroup);
        darkThemeRadioButton.setToggleGroup(themeToggleGroup);
        nordLightRadioButton.setToggleGroup(themeToggleGroup);
        nordDarkThemeRadioButton.setToggleGroup(themeToggleGroup);
        cupertinoDarkRadioButton.setToggleGroup(themeToggleGroup);
        draculaRadioButton.setToggleGroup(themeToggleGroup);
    }

    private void applyTheme() {
        Toggle selected = themeToggleGroup.getSelectedToggle();
        if (selected == null) return;

        String selectedTheme;
        if (selected == lightThemeRadioButton) {
            selectedTheme = new PrimerLight().getUserAgentStylesheet();
        } else if (selected == darkThemeRadioButton) {
            selectedTheme = new PrimerDark().getUserAgentStylesheet();
        } else if (selected == nordLightRadioButton) {
            selectedTheme = new NordLight().getUserAgentStylesheet();
        } else if (selected == nordDarkThemeRadioButton) {
            selectedTheme = new NordDark().getUserAgentStylesheet();
        } else if (selected == cupertinoDarkRadioButton) {
            selectedTheme = new CupertinoDark().getUserAgentStylesheet();
        } else if (selected == draculaRadioButton) {
            selectedTheme = new Dracula().getUserAgentStylesheet();
        } else {
            selectedTheme = new PrimerLight().getUserAgentStylesheet();
        }

        Application.setUserAgentStylesheet(selectedTheme);
        setTheme(selectedTheme);
    }

    private void setTheme(String userAgentStylesheet) {
        theme = userAgentStylesheet;
        ThemeManager.setCurrentTheme(userAgentStylesheet);
    }
}
