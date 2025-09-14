    plugins {
      application
      java
    }

    dependencies {
      implementation(project(":domain"))
      implementation("com.h2database:h2:2.3.232")

      testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
      testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    extensions.configure<org.gradle.api.plugins.JavaApplication> {
      mainClass.set("com.dpandev.client.ClientApp")
    }
    