# 保留 Compose 编译期生成的信息，避免 release 包崩溃
-keep class androidx.compose.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
# 数据类若被反射使用可保留
-keep class com.gncal.app.model.** { *; }
