version = "0.1"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.rpc.platform)
}

kotlin {
    js(IR) {
        generateTypeScriptDefinitions()
        useEsModules()

        browser {
            binaries.library()

            commonWebpackConfig {
                sourceMaps = true
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
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