plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
}

android {

  buildFeatures {
    viewBinding = true
  }

  namespace = "com.example.ipsapp"
  namespace = "com.example.ipsapp"
  compileSdk = 33

  defaultConfig {
    applicationId = "com.example.ipsapp"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }

  packagingOptions {
    resources.excludes.add("META-INF/*")
  }
}

dependencies {

  implementation("androidx.core:core-ktx:1.10.1")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.9.0")
  implementation("com.google.android.gms:play-services-vision:20.1.3")
  implementation ("org.bitbucket.b_c:jose4j:0.7.7")
  implementation("com.google.android.fhir:engine:0.1.0-beta03")
  implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.7")

  implementation("com.google.code.gson:gson:2.9.1")

  implementation("io.jsonwebtoken:jjwt-api:0.10.5")


  implementation("com.google.zxing:core:3.4.1")
  implementation("androidx.test:core-ktx:1.5.0")
  implementation("androidx.test.ext:junit-ktx:1.1.5")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
  implementation("com.nimbusds:nimbus-jose-jwt:9.31")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  testImplementation("org.robolectric:robolectric:4.7.3")
}