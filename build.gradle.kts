import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.30"
}

group = "com.itransition.personer"
version = "1.0"

application {
    mainClassName = "com.itransition.personer.ApplicationKt"
}

repositories {
    jcenter()
    maven("https://packages.atlassian.com/maven/repository/public")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.atlassian.jira", "jira-rest-java-client-app", "5.2.2")
    implementation("com.natpryce", "konfig", "1.6.10.0")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm", "1.4.2")
}
