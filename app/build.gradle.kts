import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application") version "8.1.1"
    id("org.jetbrains.kotlin.android") version "1.8.10"
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.8.10"
    id("org.jetbrains.kotlin.kapt") version "1.8.10"
    id("com.google.dagger.hilt.android") version "2.44"
    id("com.google.firebase.crashlytics") version "2.9.5"
    id("com.google.gms.google-services") version "4.3.15"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
    id("androidx.navigation.safeargs") version "2.5.3"
    id("com.google.firebase.appdistribution") version "4.0.0"
}

ktlint {
    android.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
    filter {
        exclude("**/generated/**")
        include("**/java/**")
    }
}

kotlin {
    jvmToolchain(17)
}

val versionProps = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "version.properties")))
}

android {
    namespace = "com.wafflestudio.siksha2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wafflestudio.siksha2"
        minSdk = 26
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("mode")

    productFlavors {
        create("staging") {
            applicationIdSuffix = ".staging"

            val propertyVersionName = versionProps.getProperty("sikshaVersion")
            versionCode = SemVer.sementicVersionToSerializedCode(propertyVersionName).toInt()
            versionName = propertyVersionName

            firebaseAppDistribution {
                artifactType = "APK"
                serviceCredentialsFile = "app/src/staging/app-distribution-service-account.json"
                releaseNotes = "Staging build"
            }
        }

        create("live") {
            val propertyVersionName = versionProps.getProperty("sikshaVersion")
            versionCode = SemVer.sementicVersionToSerializedCode(propertyVersionName).toInt()
            versionName = propertyVersionName

            firebaseAppDistribution {
                artifactType = "APK"
                serviceCredentialsFile = "app/src/live/app-distribution-service-account.json"
                releaseNotes = "Live build"
            }
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore/android.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Android arch lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")

    // AAC Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")

    // Room
    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.47")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // misc
    implementation("com.github.woxthebox:draglistview:1.7.2")
    implementation("com.kakao.sdk:v2-user:2.4.1")
    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation("com.airbnb.android:lottie:5.2.0")

    // BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // Image Compression
    implementation("id.zelory:compressor:3.0.1")

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Compose
    implementation("androidx.compose.runtime:runtime:${Deps.Version.Compose}")
    implementation("androidx.compose.ui:ui:${Deps.Version.Compose}")
    implementation("androidx.compose.ui:ui-tooling:${Deps.Version.Compose}")
    implementation("androidx.compose.material:material:${Deps.Version.Compose}")
    implementation("androidx.compose.foundation:foundation:${Deps.Version.ComposeFoundation}")
    implementation("androidx.compose.foundation:foundation-layout:${Deps.Version.ComposeFoundation}")
    implementation("androidx.compose.runtime:runtime-livedata:${Deps.Version.Compose}")
    implementation("androidx.paging:paging-compose:${Deps.Version.PagingCompose}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Deps.Version.ComposeViewModel}")
    implementation("androidx.hilt:hilt-navigation-compose:${Deps.Version.ComposeHiltNavigation}")

    // Paging
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")
}
