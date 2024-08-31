plugins {
    alias(libs.plugins.kotlinx.rpc.platform)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "org.kmp"
version = "1.0.0"
application {
    mainClass.set("org.kmp.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.kotlinx.rpc.krpc.server)
    implementation(libs.kotlinx.rpc.krpc.serialization.json)
    implementation(libs.kotlinx.rpc.krpc.ktor.server)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation("com.h2database:h2:2.2.224")

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}