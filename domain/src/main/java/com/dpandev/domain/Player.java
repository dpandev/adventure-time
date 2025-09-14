package com.dpandev.domain;

import java.util.UUID;

public class Player {
  private final UUID id = UUID.randomUUID();
  private String name;

  public Player(String name) {
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
