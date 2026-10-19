plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.offlinerouteguide"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.offlinerouteguide"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    // kotlin serialization
    implementation(libs.kotlinx.serialization.json)
    // google play service api for fusedLocation
    implementation(libs.play.services.location)
    // compose lifecycle dependency for android view model
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // map libre compose map library
    implementation("org.maplibre.compose:maplibre-compose:0.13.1")
    // Retrofit Http Client
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Converter to parse JSON into Kotlin objects
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // pmrtiles reader
//    implementation("com.github.MinotaurG:pmtiles-kotlin:0.1.0")
    implementation("ch.poole.geo.pmtiles-reader:Reader:0.3.7")

    implementation("io.github.rallista:valhalla-mobile:0.1.1")

}