plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "kubernetes-informer-coordination-demo"
includeBuild("../../kotlin-event-sourcing")