plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.weather_api_dummy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weather_api_dummy"
        minSdk = 21
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //retrofit2
 //   def retrofit_version = "2.9.0"
    implementation (libs.retrofit)
    implementation (libs.retrofit2.converter.gson)

   // def okhttp = '3.10.0'
    implementation (libs.okhttp)
    implementation (libs.okhttp3.logging.interceptor)

   // def lifecycleVersion = '2.2.0-alpha02'
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.lifecycle.viewmodel.ktx)

    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.retrofit2.kotlin.coroutines.adapter)

    implementation (libs.play.services.location)

    implementation (libs.glide)
    implementation (libs.androidx.activity.ktx)
    implementation (libs.androidx.lifecycle.viewmodel.ktx.v261)
    testImplementation (libs.mockk.mockk)
    testImplementation (libs.jetbrains.kotlinx.coroutines.test)
    testImplementation (libs.androidx.core.testing) // For LiveData testing
    testImplementation (libs.robolectric)


}