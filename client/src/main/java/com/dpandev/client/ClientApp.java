package com.dpandev.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientApp {
  public static void main(String[] args) throws Exception {
    System.out.println("Adventure Time — CLI");
    var in = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      System.out.print("> ");
      var line = in.readLine();
      if (line == null || line.equalsIgnoreCase("quit")) {
        break;
      }
      System.out.println("You typed: " + line);
    }
  }
}
