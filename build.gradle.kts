plugins {
    `java-library`
}

group = "io.github.progmise"
version = "0.2.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.21.7")
    api("com.fasterxml.jackson.core:jackson-core:2.21.7")
    api("org.springframework:spring-web:6.2.19")
    api("org.springframework:spring-context:6.2.19")
    api("org.springframework.hateoas:spring-hateoas:2.5.3")
    api("org.springframework.boot:spring-boot-autoconfigure:3.5.14")
    api("org.togglz:togglz-core:4.4.0")
    api("org.redisson:redisson:3.37.0")
    api("org.slf4j:slf4j-api:2.0.17")
    compileOnlyApi("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.7")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.7")
    implementation("io.micrometer:micrometer-core:1.15.12")

    // Force fixed versions on transitive dependencies (Trivy findings).
    implementation(platform("io.netty:netty-bom:4.1.137.Final"))
    constraints {
        implementation("net.minidev:json-smart:2.5.2") {
            because("CVE-2024-57699")
        }
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
