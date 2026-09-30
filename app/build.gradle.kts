import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // AGP 9 起内置 Kotlin 支持，无需再应用 org.jetbrains.kotlin.android
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.gncal.app"
    // 使用最新 SDK 编译；targetSdk 仍锁定 Android 16（API 36）
    compileSdk = 37
    buildToolsVersion = "37.0.0"

    defaultConfig {
        applicationId = "com.gncal.app"
        minSdk = 26
        // Android 16（API 36）为目标平台，启用 edge-to-edge、预测返回等新特性
        targetSdk = 36
        versionCode = 5
        versionName = "1.4.0"
    }

    // 签名配置：读取根目录 keystore.properties；若文件不存在则回退到调试签名，
    // 保证 assembleRelease 始终产出可直接安装的 APK。
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val releaseProps: Properties? = if (keystorePropertiesFile.exists()) {
        Properties().apply {
            keystorePropertiesFile.inputStream().use { stream -> load(stream) }
        }
    } else {
        null
    }

    signingConfigs {
        releaseProps?.let { props ->
            create("release") {
                storeFile = file(props.getProperty("storeFile"))
                storePassword = props.getProperty("storePassword")
                keyAlias = props.getProperty("keyAlias")
                keyPassword = props.getProperty("keyPassword")
                // 全量签名方案：v1 兼容 Android 7 以下，v2 为 Android 7+ 必需，
                // v3/v3.1 让 Android 9+/13+ 走更强的校验并支持密钥轮换，
                // v4 供 adb 增量安装（adb install --incremental）使用。
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = false
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        // 适配 Android 15+ 的 16KB 内存页：不要使用旧版（未对齐）的 so 打包方式
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.wsc)
    implementation(libs.androidx.compose.material.icons.extended)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
}
