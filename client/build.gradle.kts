import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

version = "0.1"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js {
        generateTypeScriptDefinitions()
        useEsModules()

        browser {
            binaries.library()

            commonWebpackConfig {
                sourceMaps = true
            }
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.add("-Xstrict-implicit-export-types")
        }
    }

    sourceSets {
        commonMain {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")

            dependencies {
                implementation(projects.shared)

                implementation(libs.ktor.client.js)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.websockets)
                implementation(libs.kotlinx.rpc.krpc.client)
                implementation(libs.kotlinx.rpc.krpc.serialization.json)
                implementation(libs.kotlinx.rpc.krpc.ktor.client)
            }
        }
    }
}
