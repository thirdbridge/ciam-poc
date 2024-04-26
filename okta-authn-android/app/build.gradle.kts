plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.oktaauthnintegration"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.oktaauthnintegration"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["webAuthenticationRedirectScheme"] = "com.example.oktaauthnintegration:/login"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    packaging {
        resources.pickFirsts.add("META-INF/okta/version.properties")
        resources.pickFirsts.add("META-INF/DEPENDENCIES")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.compose.runtime:runtime-livedata")

    // Ensure all dependencies are compatible using the Bill of Materials (BOM).
    implementation(platform("com.okta.kotlin:bom:1.2.0"))
    implementation("com.okta.kotlin:auth-foundation")
    implementation("com.okta.kotlin:auth-foundation-bootstrap")
    implementation("com.okta.kotlin:oauth2")

    implementation("com.okta.authn.sdk:okta-authn-sdk-api:2.0.11")
    runtimeOnly("com.okta.authn.sdk:okta-authn-sdk-impl:2.0.11") {
        exclude(group = "com.okta.sdk", module = "okta-sdk-httpclient")
        exclude(group = "org.bouncycastle")
    }
    implementation("com.okta.sdk:okta-sdk-okhttp:8.2.5")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2")

}