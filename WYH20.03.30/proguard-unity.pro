-keep class bitter.jnibridge.* { *; }
-keep class com.unity3d.player.* { *; }
-keep class org.fmod.* { *; }
-keep class com.vuforia.* { *; }
-ignorewarnings
-dontwarn com.unity3d.player.**
-dontwarn org.fmod.**
-keep class com.unity3d.player.*{*;}
-keep class org.fmod.*{*;}
-keep public class * extends com.unity3d.player.**
-keep public class * extends org.fmod.**

-libraryjars D:\work\android\workspace\wyh_visitor_android\WYH20.03.30\libs\unity-classes.jar

