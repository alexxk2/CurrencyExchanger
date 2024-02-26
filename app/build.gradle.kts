plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    //Kotlin symbol processing  for Room
    id("com.google.devtools.ksp")
    //Hilt
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.currencyexchanger"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.currencyexchanger"
        minSdk = 26
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    val retrofit = "2.9.0"
    val glide = "4.15.0"
    val navigation = "2.7.6"
    val lifecycle = "2.7.0"
    val room = "2.5.2"
    val hilt = "2.50"
    val coreKtx = "1.12.0"
    val appCompat = "1.6.1"
    val material = "1.11.0"
    val constraintLayout = "2.1.4"
    val junit = "4.13.2"
    val androidxJunit ="1.1.5"
    val espresso = "3.5.1"
    val gson = "2.10.1"
    val splashScreen = "1.0.1"
    val fragment = "1.6.2"
    val tinkoffDecoro = "1.3.1"

    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.appcompat:appcompat:$appCompat")
    implementation("com.google.android.material:material:$material")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayout")
    testImplementation("junit:junit:$junit")
    androidTestImplementation("androidx.test.ext:junit:$androidxJunit")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espresso")

    //retrofit & Gson converter
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")

    //Gson
    implementation("com.google.code.gson:gson:$gson")

    //Splash screen
    implementation("androidx.core:core-splashscreen:$splashScreen")

    //glide
    implementation ("com.github.bumptech.glide:glide:$glide")
    annotationProcessor ("com.github.bumptech.glide:compiler:$glide")

    // Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:$navigation")
    implementation ("androidx.navigation:navigation-ui-ktx:$navigation")

    // ViewModel & LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
    implementation ("androidx.fragment:fragment-ktx:$fragment")

    // Room
    implementation ("androidx.room:room-runtime:$room")
    implementation ("androidx.room:room-ktx:$room")
    annotationProcessor("androidx.room:room-compiler:$room")

    //Kotlin symbol processing for Room
    ksp("androidx.room:room-compiler:$room")

    //hilt
    implementation("com.google.dagger:hilt-android:$hilt")
    kapt("com.google.dagger:hilt-compiler:$hilt")

    //phone mask
    implementation("ru.tinkoff.decoro:decoro:$tinkoffDecoro")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}