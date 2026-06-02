package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class UserValidator {
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_.-]+$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    public static ValidationResult isUsernameValid(String username) {
        List<String> errors = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) {
            errors.add("Ім'я користувача не може бути порожнім");
            return new ValidationResult(false, errors);
        }
        if (username.length() < MIN_USERNAME_LENGTH) {
            errors.add("Ім'я користувача має бути не менше " + MIN_USERNAME_LENGTH + " символів");
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            errors.add("Ім'я користувача не може перевищувати " + MAX_USERNAME_LENGTH + " символів");
        }
        if (!Pattern.matches(USERNAME_PATTERN, username)) {
            errors.add("Ім'я користувача може містити лише латинські літери, цифри та символи _.-");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isUsernameUnique(String username, UUID userId, UserRepository repository) {
        ValidationResult basicValidation = isUsernameValid(username);
        if (!basicValidation.isValid()) return basicValidation;

        List<String> errors = new ArrayList<>();
        repository.findByUsername(username).ifPresent(existingUser -> {
            if (userId == null || !existingUser.userId().equals(userId)) {
                errors.add("Користувач з таким ім'ям вже існує");
            }
        });
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isPasswordValid(String password) {
        List<String> errors = new ArrayList<>();
        if (password == null || password.isEmpty()) {
            errors.add("Пароль не може бути порожнім");
            return new ValidationResult(false, errors);
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.add("Пароль має бути не менше " + MIN_PASSWORD_LENGTH + " символів");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            errors.add("Пароль не може перевищувати " + MAX_PASSWORD_LENGTH + " символів");
        }
        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            errors.add("Пароль має містити хоча б одну цифру, одну малу та одну велику літеру");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isEmailValid(String email) {
        List<String> errors = new ArrayList<>();
        if (email == null || email.isEmpty()) {
            errors.add("Email не може бути порожнім");
            return new ValidationResult(false, errors);
        }
        if (email.length() > 100) {
            errors.add("Email не може перевищувати 100 символів");
        }
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            errors.add("Некоректний формат email");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isUserValid(User user, boolean isExisting, UserRepository repository) {
        List<String> errors = new ArrayList<>();
        
        ValidationResult usernameResult = isUsernameUnique(user.username(), user.userId(), repository);
        if (!usernameResult.isValid()) errors.addAll(usernameResult.getErrors());

        ValidationResult passwordResult = isPasswordValid(user.password());
        if (!passwordResult.isValid()) errors.addAll(passwordResult.getErrors());

        ValidationResult emailResult = isEmailValid(user.email());
        if (!emailResult.isValid()) errors.addAll(emailResult.getErrors());

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
