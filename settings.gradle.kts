    rootProject.name = "adventure-time"

    pluginManagement {
      repositories {
        gradlePluginPortal()
        mavenCentral()
      }
    }

    include(":domain", ":client", ":server")
    