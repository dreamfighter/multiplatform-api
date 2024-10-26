plugins {
    kotlin("multiplatform")
}

kotlin {

    jvm()

    sourceSets {

        val jvmMain by getting {
            dependencies {
                implementation(libs.ksp.processor.api)
                implementation(libs.kotlinpoet)
                implementation(project(":annotation"))
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }

    }
}
