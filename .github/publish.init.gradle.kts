import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.api.tasks.javadoc.Javadoc

// Publishing init script — applied only in CI:
//   ./gradlew publishToMavenCentral -I .github/publish.init.gradle.kts
//
// Injects the vanniktech maven-publish plugin, GPG signing and all POM
// metadata at publish time, so build.gradle.kts stays free of any
// publishing configuration.
//
// Credentials are read from ORG_GRADLE_PROJECT_* environment variables:
//   mavenCentralUsername / mavenCentralPassword   (Central Portal user token)
//   signingInMemoryKey / signingInMemoryKeyPassword (ASCII-armored GPG key)

initscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.34.0")
    }
}

allprojects {
    apply<MavenPublishPlugin>()

    tasks.withType<Javadoc>().configureEach {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(automaticRelease = true)
        signAllPublications()

        pom {
            name.set("api-utils")
            description.set("Shared utility library for Spring Boot APIs: error contract, validators, HAL pagination, Redis cache and Togglz support.")
            inceptionYear.set("2025")
            url.set("https://github.com/progmise/api-utils")

            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("progmise")
                    name.set("Leonel Chaile")
                    email.set("leonel.chaile@gmail.com")
                }
            }

            scm {
                url.set("https://github.com/progmise/api-utils")
                connection.set("scm:git:git://github.com/progmise/api-utils.git")
                developerConnection.set("scm:git:ssh://git@github.com/progmise/api-utils.git")
            }
        }
    }
}
