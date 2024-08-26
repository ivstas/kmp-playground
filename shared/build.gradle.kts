plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.kotlinPluginSerialization)
}

kotlin {
    js {
        browser()
    }
    
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            implementation(libs.kotlinx.rpc.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

