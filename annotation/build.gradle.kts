import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.androidLibrary)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs{
        browser()
        nodejs()
    }
    androidTarget()
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()
}

android {
    namespace = "id.dreamfighter.multiplatform.annotation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}