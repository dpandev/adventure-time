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

    // Configure JVM arguments for the run task to ensure memory use does not exceed 512MB
    tasks.named<JavaExec>("run") {
      jvmArgs = listOf(
        "-Xms128m", "-Xmx256m",
        "-XX:+UseG1GC",
        "-Xlog:gc*,safepoint",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-XX:HeapDumpPath=${project.layout.buildDirectory.get().asFile}/heapdumps"
      )
    }
