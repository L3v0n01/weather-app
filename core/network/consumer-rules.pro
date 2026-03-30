# Keep Retrofit service interfaces
-keepattributes *Annotation*
-keep class com.la.weather.core.network.api.** { *; }

# Keep Kotlin Serialization @Serializable DTO classes
-keep class com.la.weather.core.network.dto.** { *; }
-keepclassmembers class com.la.weather.core.network.dto.** { *; }

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
