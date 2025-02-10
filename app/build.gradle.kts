plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.asthafood"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.asthafood"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
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
    buildFeatures{
        buildConfig = true
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

    implementation ("com.daimajia.easing:library:2.0@aar")
    implementation ("com.daimajia.androidanimations:library:2.3@aar")

    implementation ("com.android.volley:volley:1.2.0")

    implementation (files("libs/jtds-1.3.1.jar"))
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")



}