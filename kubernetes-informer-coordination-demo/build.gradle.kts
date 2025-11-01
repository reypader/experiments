plugins {
    kotlin("jvm") version "2.2.21"
    id("application")
    id("com.google.cloud.tools.jib") version "3.4.5"
}

group = "com.rmpader"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.rmpader:coordination-kubernetes:1.0-SNAPSHOT")
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.10.2")
    implementation("io.kubernetes:client-java:24.0.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.rmpader.experiments.MainKt")
}
// Jib configuration
jib {
    from {
        image = "eclipse-temurin:21-jre-alpine"
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
        }
    }

    to {
        image = "kubernetes-informer-coordination-demo"
        tags = setOf("latest", version.toString())
    }

    container {
        mainClass = application.mainClass.get()
        jvmFlags =
            listOf(
                "-XX:+UseContainerSupport",
            )
        ports = emptyList()

        // Set the user to run as non-root
        user = "1000:1000"

        // Environment variables (can be overridden in K8s)
        environment =
            mapOf(
                "JAVA_TOOL_OPTIONS" to "-XX:+ExitOnOutOfMemoryError",
            )

        creationTime = "USE_CURRENT_TIMESTAMP"
    }
}
