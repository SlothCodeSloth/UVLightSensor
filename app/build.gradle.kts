plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.bletest3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bletest3"
        minSdk = 34
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Chip Navigation
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


}