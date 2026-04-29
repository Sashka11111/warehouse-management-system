package com.liamtseva.warehousemanagementsystem.domain.security;

import com.liamtseva.warehousemanagementsystem.persistence.entity.User;

public class AuthenticatedUser {
  private static AuthenticatedUser instance;
    private User currentUser;

    private AuthenticatedUser() {}

    public static AuthenticatedUser getInstance() {
    if (instance == null) {
      instance = new AuthenticatedUser();
    }
    return instance;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }
}
