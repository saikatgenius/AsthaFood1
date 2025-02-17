plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures{
        buildConfig = true
        viewBinding = true
    }
}

dependencies {


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    implementation(libs.glide)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.exoplayer)

    implementation (libs.circleimageview)
    implementation ("com.google.android.material:material:1.12.0")

    implementation("com.android.volley:volley:1.2.1")
    implementation("com.itextpdf:itextg:5.5.10")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.airbnb.android:lottie:6.4.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.10.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("com.github.MrNouri:DynamicSizes:1.0")
    implementation ("com.daimajia.easing:library:2.0@aar")
    implementation ("com.daimajia.androidanimations:library:2.3@aar")
    implementation("net.sourceforge.jtds:jtds:1.3.1")
    implementation ("com.github.CanHub:Android-Image-Cropper:4.3.1")

    implementation ("de.hdodenhof:circleimageview:3.1.0")


}