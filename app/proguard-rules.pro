# InvestigaWarma - reglas ProGuard/R8
# La app no ofusca en debug. Reglas mínimas para un eventual build release.

-keep class com.investigawarma.app.data.local.entity.** { *; }
-keepclassmembers class com.investigawarma.app.data.local.entity.** { *; }
-dontwarn kotlinx.serialization.**
