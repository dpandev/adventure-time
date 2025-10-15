import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import java.io.File

plugins {
  id("org.springframework.boot") version "3.5.5" apply false
  id("io.spring.dependency-management") version "1.1.7" apply false
  id("com.diffplug.spotless") version "8.0.0" apply false
}

allprojects {
  group = "com.dpandev"
  version = "0.1.0-SNAPSHOT"
  repositories { mavenCentral() }
}

subprojects {
  plugins.withId("java") {
    tasks.withType<Javadoc>().configureEach {
      // write each module's Javadoc into /docs/api/<module>
      val outDir: File = rootProject.layout
        .projectDirectory
        .dir("docs/dev/api/javadoc/${project.name}")
        .asFile

      setDestinationDir(outDir)

      (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        addBooleanOption("html5", true)
      }
    }
  }
  apply(plugin = "java")
  // ---- Java toolchain (JDK 21) ----
  extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
  }
  tasks.withType<Test>().configureEach { useJUnitPlatform() }

  // ---- Spotless (auto-format) ----
  apply(plugin = "com.diffplug.spotless")
  extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
      // Google Java Format (don’t specify version unless you need to)
      googleJavaFormat()
      // Optional: enforce license header or import order here if you want
      target("**/*.java")
      // Exclude generated sources, if any:
      targetExclude("**/build/**", "**/generated/**")
    }
  }

  // ---- Checkstyle (style rules) ----
  apply(plugin = "checkstyle")
  extensions.configure<org.gradle.api.plugins.quality.CheckstyleExtension> {
    // Pin a modern Checkstyle engine; adjust if you prefer another
    toolVersion = "10.17.0"
    configFile = file("$rootDir/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false     // fail the build on violations
    maxWarnings = 0              // treat warnings as errors
  }
  tasks.withType<org.gradle.api.plugins.quality.Checkstyle>().configureEach {
    reports.html.required.set(true)
    reports.xml.required.set(false)
  }

  // Make the standard `check` task also run style checks
  tasks.named("check") {
    dependsOn("spotlessCheck", "checkstyleMain", "checkstyleTest")
  }
}

// Aggregate helper so one command builds all module javadocs
tasks.register("javadocAll") {
  dependsOn(subprojects.map { it.tasks.named("javadoc") })
}
