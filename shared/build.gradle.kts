import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.rpc) // not platform, it is needed for codegen
    alias(libs.plugins.kotlinPluginSerialization)
}

kotlin {
    js {
//        generateTypeScriptDefinitions()
//        useEsModules()
        browser {
//            binaries.library()
//
//            commonWebpackConfig {
//                sourceMaps = true
//            }
        }
//
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        compilerOptions {
//            freeCompilerArgs.add("-Xstrict-implicit-export-types")
//        }
    }
    
    jvm()
    
    sourceSets {
        all {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
        }
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            implementation(libs.kotlinx.rpc.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

