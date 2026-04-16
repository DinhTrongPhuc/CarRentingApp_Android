# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Models (Firestore serialization)
-keep class com.example.carrentingapp.models.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }

# General
-keepattributes Signature
-keepattributes *Annotation*
