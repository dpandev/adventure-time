    plugins {
      application
      java
    }

    dependencies {
      implementation(project(":domain"))
      implementation("com.h2database:h2:2.4.240")

      testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
      testRuntimeOnly("org.junit.platform:junit-platform-launcher")
      testImplementation("org.mockito:mockito-core:5.20.0")
      testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
    }

    extensions.configure<org.gradle.api.plugins.JavaApplication> {
      mainClass.set("com.dpandev.client.runtime.ClientApp")
    }

    // Configure JVM arguments for the run task
    tasks.named<JavaExec>("run") {
      jvmArgs = listOf(
        "-Xms512m",
        "-Xmx2048m",  // Increased to 2GB for larger worldpacks
        "-XX:+UseG1GC"
      )

      // Configure stdin/stdout properly for interactive mode
      standardInput = System.`in`
      standardOutput = System.out
      errorOutput = System.err

      // Set working directory to project root so saves go to ./saves/
      workingDir = project.rootDir

      // Ensure we don't inherit problematic JVM args
      systemProperty("java.awt.headless", "true")
    }

    // Add custom task for running interactively with better console support
    tasks.register<JavaExec>("runInteractive") {
      group = "application"
      description = "Runs the application interactively with full console support"
      classpath = sourceSets["main"].runtimeClasspath
      mainClass.set("com.dpandev.client.runtime.ClientApp")

      // Copy JVM args from run task
      jvmArgs = listOf(
        "-Xms512m",
        "-Xmx2048m",
        "-XX:+UseG1GC"
      )

      // Configure for interactive mode
      standardInput = System.`in`
      standardOutput = System.out
      errorOutput = System.err

      // Set working directory to project root so saves go to ./saves/
      workingDir = project.rootDir

      // Pass through args
      if (project.hasProperty("appArgs")) {
        args = (project.property("appArgs") as String).split("\\s+".toRegex())
      }
    }
