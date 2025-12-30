// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Note: KSP and Hilt are NOT declared here to avoid classloader issues (GitHub #3965)
// They are declared directly in app/build.gradle.kts using id() syntax
buildscript {
    dependencies {
        // Force JavaPoet version for Hilt plugin compatibility
        classpath("com.squareup:javapoet:1.13.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

