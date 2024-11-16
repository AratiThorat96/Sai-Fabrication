// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Add the classpath for Google services plugin
        classpath ("com.google.gms:google-services:4.4.2") // This is correct
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Remove the apply false for google.gms plugin since we are using the classpath method
    // alias(libs.plugins.google.gms.google.services) apply false
}
