# ProGuard/R8 rules for HoneyPie Launcher

# Keep the launcher activity
-keep class com.honeypie.launcher.MainActivity { *; }

# Strip Kotlin metadata to save space
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
