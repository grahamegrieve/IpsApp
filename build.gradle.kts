// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.1.2" apply false
  id("org.jetbrains.kotlin.android") version "1.8.0" apply false
  id("com.android.library") version "8.1.2" apply false
}
buildscript {
  dependencies {
    classpath("com.google.gms:google-services:4.3.10") // Use the latest version
  }
}
