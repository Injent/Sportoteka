plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "ru.master.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.master.app"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        resourceConfigurations += setOf(
            "en",
            "ru",
            "af",
            "ar",
            "be",
            "bn",
            "cs",
            "da",
            "de",
            "es",
            "eu",
            "fa",
            "fil",
            "fr",
            "hi",
            "hu",
            "ia",
            "in",
            "it",
            "iw",
            "ja",
            "kk",
            "kn",
            "ko",
            "nl",
            "pl",
            "pt",
            "pt-rBR",
            "ro",
            "sk",
            "sr",
            "te",
            "th",
            "tr",
            "uk",
            "vi",
            "zh-rCN",
            "zh-rTW"
        )
    }

    signingConfigs {
        register("release") {
            storeFile = rootProject.file("release.jks")
            storePassword = "123456789"
            keyAlias = "key0"
            keyPassword = "123456789"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.icons.extended)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization)

    implementation(libs.datastore)
    implementation(libs.koin.workManager)
    implementation(libs.koin.navigation)
    implementation(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.contentNegotiation)
    implementation(libs.ktor.kotlinx.json)
    implementation(libs.ktor.auth)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewModelCompose)
    implementation(libs.lifecycle.viewModelKtx)
    implementation(libs.work)
    implementation(libs.sheetsComposeDialog.core)
    implementation(libs.sheetsComposeDialog.calendar)
    implementation(libs.charts)
    implementation(libs.filekit.core)
    implementation(libs.filekit.compose)
    implementation(libs.camerak)
    implementation(libs.camerak.images)
    implementation(libs.camerak.qr)
    implementation(libs.javaTime)
    implementation(libs.infoBar)
    implementation(libs.bonsai.core)
    implementation(libs.lazyPaginationCompose)

    implementation(platform(libs.rustore.bom))
    implementation(libs.rustore.pushclient)
    implementation(libs.rustore.auth)
}