import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.kotlin.kapt")  version "1.5.0-M1"
    application
}

group = "me.blade"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}



tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}

var arrow_version = "0.11.0"
dependencies {
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("io.arrow-kt:arrow-fx:$arrow_version")
    implementation("io.arrow-kt:arrow-ui:$arrow_version")
    implementation("io.arrow-kt:arrow-syntax:$arrow_version")
    kapt ("io.arrow-kt:arrow-meta:$arrow_version")
}