# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# === Retrofit & OkHttp ===
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# === Gson ===
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keepclassmembers class * extends com.google.gson.reflect.TypeToken { *; }
# Keep model classes used by Gson
-keep class com.practicum.playlistmaker.search.data.** { *; }
-keep class com.practicum.playlistmaker.new_playlist.data.** { *; }

# === Kotlinx Serialization ===
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
    @kotlinx.serialization.Serializable <fields>;
}
-keep class * extends kotlinx.serialization.KSerializer { *; }

# === Room ===
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# === Koin ===
-keep class * extends org.koin.core.module.Module { *; }
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# === Navigation ===
-keep class * extends androidx.navigation.NavArgs { *; }
-keep class androidx.navigation.** { *; }

# === Glide ===
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# === Parcelable ===
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# === Keep domain models ===
-keep class com.practicum.playlistmaker.search.domain.** { *; }
-keep class com.practicum.playlistmaker.new_playlist.domain.** { *; }
-keep class com.practicum.playlistmaker.player.domain.** { *; }
-keep class com.practicum.playlistmaker.settings.domain.** { *; }
-keep class com.practicum.playlistmaker.sharing.domain.** { *; }

# === Keep ViewModels and Fragments (Koin + Navigation) ===
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }
-keep class * extends androidx.fragment.app.Fragment { <init>(...); }
-keep class * extends android.app.Activity { <init>(...); }

# === Remove logging in release ===
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
