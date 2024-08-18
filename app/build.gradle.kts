plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)

}

android {
    namespace = "com.example.inventorymanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.inventorymanager"
        minSdk = 26
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
//Firebase:
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))

    //Firebase Authentication:
    implementation (libs.firebase.ui.auth)

    //Firebase Database
    implementation(libs.firebase.database)
    implementation (libs.appcompat.v161)
    implementation (libs.fragment)

    //Firebase Storage
    implementation(libs.firebase.storage)
}