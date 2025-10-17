# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ==============================================================================
# DEBUGGING & STACK TRACES
# ==============================================================================
# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations for reflection
-keepattributes *Annotation*

# ==============================================================================
# PROGUARD DEBUG OUTPUT (for troubleshooting)
# ==============================================================================
# Uncomment these lines to generate debug files that help identify what's being stripped:
# -printconfiguration proguard-config.txt
# -printusage proguard-usage.txt
# -printmapping proguard-mapping.txt

# ==============================================================================
# HILT / DAGGER
# ==============================================================================
# Critical for dependency injection - Hilt uses reflection to instantiate ViewModels,
# factories, and components. Without these rules, app will crash on launch when trying
# to inject dependencies.

# Keep Hilt core classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep all classes annotated with @HiltViewModel (critical for ViewModels)
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Hilt generated modules and components
-keep class **_HiltModules { *; }
-keep class **_HiltModules$** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManager { *; }

# Keep Hilt ViewModel factories
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory$ViewModelFactoriesEntryPoint { *; }

# Keep Dagger generated code (factories and member injectors)
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Keep fragment and activity context wrappers
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }

# Suppress Hilt warnings
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**

# ==============================================================================
# PROTOBUF (JAVALITE)
# ==============================================================================
# Critical for Proto DataStore - protobuf-javalite generates classes at compile time.
# R8 will strip these classes if not protected, causing crashes when deserializing data.

# Keep all protobuf base classes
-keep class com.google.protobuf.** { *; }

# Keep all generated protobuf message classes
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

# Keep all fields and methods in protobuf messages (needed for serialization)
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
    <methods>;
}

# Keep app-specific proto classes (generated from game_state.proto)
-keep class com.soyvictorherrera.scorecount.GameStateProto { *; }
-keep class com.soyvictorherrera.scorecount.GameStateProto$** { *; }
-keep class com.soyvictorherrera.scorecount.PlayerProto { *; }
-keep class com.soyvictorherrera.scorecount.PlayerProto$** { *; }

# Keep protobuf builders (used in serialization)
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    public static ** parseFrom(...);
    public static ** newBuilder();
    public ** build();
    public ** buildPartial();
}

# ==============================================================================
# DATASTORE
# ==============================================================================
# DataStore uses reflection to instantiate serializers. Without these rules,
# app crashes when trying to read/write preferences or proto data.

# Keep DataStore core classes
-keep class androidx.datastore.*.** { *; }

# Keep all DataStore serializers
-keepclassmembers class * extends androidx.datastore.core.Serializer {
    public <methods>;
}

# Keep app-specific serializer (GameStateSerializer)
-keep class com.soyvictorherrera.scorecount.data.datasource.GameStateSerializer { *; }
-keepclassmembers class com.soyvictorherrera.scorecount.data.datasource.GameStateSerializer {
    <init>(...);
    public <methods>;
}

# ==============================================================================
# KOTLIN COROUTINES
# ==============================================================================
# Coroutines use reflection for internal dispatchers and exception handlers.
# StateFlow initialization (used in SettingsLocalDataSource and LocalScoreDataSource)
# will crash without these rules.

# Keep coroutine dispatcher factories
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }

# Keep volatile fields used by coroutines for thread safety
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep coroutines core classes
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep Flow operators (used for StateFlow transformations)
-keep class kotlinx.coroutines.flow.** { *; }

# ==============================================================================
# ROOM DATABASE
# ==============================================================================
# Room usually includes its own ProGuard rules, but we explicitly keep critical
# classes for safety.

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase {
    <init>(...);
}

# Keep entity classes (annotated with @Entity)
-keep @androidx.room.Entity class * {
    <fields>;
    <init>(...);
}

# Keep DAO interfaces (annotated with @Dao)
-keep @androidx.room.Dao interface * {
    *;
}

# Suppress Room paging warnings (if not using paging)
-dontwarn androidx.room.paging.**

# ==============================================================================
# JETPACK COMPOSE
# ==============================================================================
# Compose uses reflection for state management and recomposition. Without these
# rules, Compose UI might crash or fail to update properly.

# Keep Compose runtime classes
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# Keep Compose compiler-generated classes
-keepclassmembers class androidx.compose.** { *; }

# Keep Composable functions metadata
-keepclassmembers @androidx.compose.runtime.Composable class * {
    *;
}

# ==============================================================================
# ANDROIDX & MATERIAL DESIGN
# ==============================================================================
# Keep core Android support library classes

# Keep Splash Screen API classes
-keep class androidx.core.splashscreen.** { *; }

# Keep Material Design components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Keep Navigation component args
-keep class androidx.navigation.** { *; }
-keepclassmembers class * extends androidx.navigation.NavArgs {
    *;
}

# ==============================================================================
# KOTLIN REFLECTION & METADATA
# ==============================================================================
# Keep Kotlin metadata for reflection (used by Kotlin libraries)
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# ==============================================================================
# SERIALIZATION (GENERAL)
# ==============================================================================
# Keep generic serialization attributes that various libraries might need
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ==============================================================================
# ENUM CLASSES
# ==============================================================================
# Keep enum classes (used throughout the app)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==============================================================================
# CUSTOM APP CLASSES
# ==============================================================================
# Keep domain models (used in serialization/deserialization)
-keep class com.soyvictorherrera.scorecount.domain.model.** { *; }

# Keep data classes (kotlinx.serialization might use reflection)
-keepclassmembers class com.soyvictorherrera.scorecount.domain.model.** {
    <fields>;
    <init>(...);
}

# ==============================================================================
# LEGACY WEBVIEW (if ever needed in future)
# ==============================================================================
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
