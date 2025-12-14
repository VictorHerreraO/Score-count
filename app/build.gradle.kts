plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android.plugin)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.soyvictorherrera.scorecount"
    compileSdk = 36

    // Add signing configuration
    signingConfigs {
        create("release") {
            // CI environment variables (from GitHub Secrets)
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
            val keyAlias = System.getenv("KEY_ALIAS")
            val keyPassword = System.getenv("KEY_PASSWORD")

            if (keystoreFile != null && File(keystoreFile).exists()) {
                storeFile = File(keystoreFile)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            } else {
                // Fallback for local development (optional)
                logger.warn("Release signing not configured. Using debug signing.")
            }
        }
    }

    defaultConfig {
        applicationId = "com.soyvictorherrera.scorecount"
        minSdk = 28
        targetSdk = 36

        // Semantic versioning using version.txt and GitHub run number
        val versionFile =
            File(rootDir, "version.txt")
        val baseVersion =
            if (versionFile.exists()) {
                versionFile.readText().trim()
            } else {
                "1.0.0"
            }

        // Parse MAJOR.MINOR.PATCH from version.txt
        val versionParts = baseVersion.split(".")
        val major = versionParts.getOrNull(0)?.toIntOrNull() ?: 1
        val minor = versionParts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = versionParts.getOrNull(2)?.toIntOrNull() ?: 0

        // Get BUILD number from GitHub Actions environment variable
        // Falls back to 1 for local development builds
        val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1

        // Calculate versionCode using formula:
        // MAJOR * 100,000,000 + MINOR * 1,000,000 + PATCH * 10,000 + BUILD
        versionCode = (major * 100_000_000) + (minor * 1_000_000) + (patch * 10_000) + buildNumber

        // Construct versionName as MAJOR.MINOR.PATCH.BUILD
        versionName = "$major.$minor.$patch.$buildNumber"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release") // Add this line
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion =
            libs.versions.compose.compiler
                .get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)

    // Jetpack Compose Dependencies - using BOM
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.navigation.compose) // Added navigation compose
    implementation(libs.androidx.hilt.navigation.compose) // Added hilt navigation compose

    // ViewModel & Lifecycle for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.57.2")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.57.2")

    // Debug implementation for UI Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.28.3"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

ktlint {
    version.set("1.5.0")
    android.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

detekt {
    toolVersion = "1.23.8"
    config.setFrom(files("$rootDir/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    baseline = file("$rootDir/detekt-baseline.xml")
    ignoreFailures = false
    parallel = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
    exclude("**/generated/**", "**/build/**")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    // Prevent test hangs
    systemProperty("junit.jupiter.execution.timeout.default", "30s")

    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

// Task to print version for CI
tasks.register("printVersionName") {
    doLast {
        println(android.defaultConfig.versionName)
    }
}
