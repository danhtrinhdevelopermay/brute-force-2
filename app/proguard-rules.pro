# Add project specific ProGuard rules here.
-keep class com.monitor.cpugpu.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
