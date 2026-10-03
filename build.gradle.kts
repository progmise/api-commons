plugins {
    `java-library`
}

group = "io.github.progmise"
version = "0.2.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    api("com.fasterxml.jackson.core:jackson-core:2.19.2")
    api("org.springframework:spring-web:6.2.9")
    api("org.springframework:spring-context:6.2.9")
    api("org.springframework.hateoas:spring-hateoas:3.0.0")
    api("org.springframework.boot:spring-boot-autoconfigure:3.5.4")
    api("org.togglz:togglz-core:4.4.0")
    api("org.redisson:redisson:3.37.0")
    api("org.slf4j:slf4j-api:2.0.17")
    compileOnlyApi("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.2")
    implementation("io.micrometer:micrometer-core:1.15.2")
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
