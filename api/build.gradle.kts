import com.android.build.gradle.internal.ide.kmp.KotlinAndroidSourceSetMarker.Companion.android
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinNativeCocoaPods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serializationPlugin)
    id("module.publication")
    id("io.github.ttypic.swiftklib") version "0.6.3"
    //id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "multiplatform-api"
        browser{
            testTask {
                useKarma{
                    useChrome()
                }
            }
        }
        binaries.executable()
    }
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    cocoapods {
        ios.deploymentTarget = "11.0"
        framework {
            baseName = "MultiplatformApi"
            isStatic = true
        }
        //pod("GoogleSignIn")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    sourceSets {
        iosMain.dependencies {
            //implementation(libs.github.mirzemehdi.google)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.kotlinx.coroutines.android)
            implementation(compose.components.resources)
            implementation(libs.ktor.client.okhttp)
            //implementation(libs.github.mirzemehdi.google)
            //implementation("com.google.devtools.ksp:symbol-processing-api:2.0.21-1.0.25")
        }
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(compose.runtime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.slf4j.simple)
                implementation(kotlin("reflect"))

                implementation(libs.ktor.server.websockets)
                implementation(libs.ktor.server.cors)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.encoding)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.test)

                implementation(libs.slf4j.simple)
                implementation(libs.kotlin.test)
                implementation(libs.system.lambda)
            }
        }
    }
    /*
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.compilations {
            val main by getting {
                cinterops {
                    create("Utils")
                }
            }
        }
    }

     */
}

android {
    namespace = "id.dreamfighter.multiplatform.api"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

swiftklib {
    create("Utils") {
        path = file("native/Utils")
        packageName("id.dreamfighter.multiplatform.swift")
    }
}