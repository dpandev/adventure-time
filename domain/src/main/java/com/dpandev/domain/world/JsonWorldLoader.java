package com.dpandev.domain.world;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class JsonWorldLoader implements WorldLoader {
  private final String resourcePath;

  public JsonWorldLoader(String resourcePath) {
    this.resourcePath = resourcePath;
  }

  @Override
  public World load() {
    try (var in = getClass().getResourceAsStream(resourcePath)) {
      // Parse json and create World object
      return null;
    } catch (IOException e) {
      // In this case, rethrow as an unchecked exception, so the caller doesn't have to handle it.
      throw new UncheckedIOException(e);
    }
  }
}
