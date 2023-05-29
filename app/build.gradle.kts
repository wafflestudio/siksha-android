plugins {
    id("com.android.application") version "8.0.2"
    id("org.jetbrains.kotlin.android") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.8.0"
    id("org.jetbrains.kotlin.kapt") version "1.8.0"
    id("com.google.dagger.hilt.android") version "2.44"
    id("com.google.firebase.crashlytics") version "2.9.5"
    id("com.google.gms.google-services") version "4.3.15"
//    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
    id("androidx.navigation.safeargs") version "2.5.3"
}

//ktlint {
//    android = true
//    reporters {
//        reporter "plain"
//        reporter "checkstyle"
//        reporter "html"
//    }
//    filter {
//        exclude("**/generated/**")
//        include("**/java/**")
//    }
//    // See https://github.com/pinterest/ktlint/issues/527
//    disabledRules = ["import-ordering", "no-wildcard-imports"]
//}

android {
    namespace = "com.wafflestudio.siksha2"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.wafflestudio.siksha2"
        minSdk = 26
        targetSdk = 33
        versionCode = 3000000
        versionName = "3.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://siksha-api.wafflestudio.com\"")
            buildConfigField("String", "PREF_KEY", "\"siksha.preference\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"http://3.36.80.144\"")
            buildConfigField("String", "PREF_KEY", "\"siksha.preference\"")
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    val kotlin_version = "1.4.31"
    val navigation_version = "2.3.4"
    val retrofit_version = "2.9.0"
    val arch_lifecycle_version = "2.3.0"
    val room_version = "2.3.0-beta03"
    val paging_version = "3.0.0-beta02"
    val glide_version = "4.12.0"

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    // Android arch lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")
    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.gms:play-services-maps:17.0.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")

    // AAC Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navigation_version")
    implementation("androidx.navigation:navigation-ui-ktx:$navigation_version")

    // Room
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0-beta01")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:$paging_version")

    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.orhanobut:logger:2.2.0")

    // misc
    implementation("com.github.woxthebox:draglistview:1.7.2")
    implementation("com.kakao.sdk:v2-user:2.4.1")
    implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation("com.airbnb.android:lottie:3.0.7")

    // BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Glide
    implementation("com.github.bumptech.glide:glide:$glide_version")

    // Image Compression
    implementation("id.zelory:compressor:3.0.1")

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
