version = "0.1"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        generateTypeScriptDefinitions()
        useEsModules()

        browser {
            binaries.library()
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared)
        }
    }
}