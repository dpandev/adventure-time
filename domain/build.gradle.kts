    plugins { `java-library` }

    dependencies {
      testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
      testRuntimeOnly("org.junit.platform:junit-platform-launcher")
      testImplementation("org.mockito:mockito-core:5.20.0")
      implementation("com.fasterxml.jackson.core:jackson-databind:2.20.+")
    }
