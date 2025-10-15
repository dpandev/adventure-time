    plugins { `java-library` }

    dependencies {
      testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
      testRuntimeOnly("org.junit.platform:junit-platform-launcher")
      testImplementation("org.mockito:mockito-core:5.20.0")
    }
