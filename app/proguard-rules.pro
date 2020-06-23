# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

#----------------------------------------------------------------------------
#---------------------------------androidx----------------------------------
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
#不混淆javabean
-keep class com.stxx.wyhvisitorandroid.bean.**{*;}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
 -keep class **.R$* {
  *;
 }
 -keepclassmembers class * {
     void *(**On*Event);
 }
 #----------------------------------------------------------------------------

 #---------------------------------webview------------------------------------
 -keepclassmembers class fqcn.of.javascript.interface.for.Webview {
    public *;
 }
 -keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
     public boolean *(android.webkit.WebView, java.lang.String);
 }
 -keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView, jav.lang.String);
 }
 #----------------------------------------------------------------------------

 #---------------------------------immersionbar------------------------------------
 -keep class com.gyf.immersionbar.* {*;}
 -dontwarn com.gyf.immersionbar.**
  #----------------------------------------------------------------------------

 #---------------------------------vLayout------------------------------------
 -keepattributes InnerClasses
 -keep class com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx { *; }
 -keep class android.support.v7.widget.RecyclerView$LayoutParams { *; }
 -keep class android.support.v7.widget.RecyclerView$ViewHolder { *; }
 -keep class android.support.v7.widget.ChildHelper { *; }
 -keep class android.support.v7.widget.ChildHelper$Bucket { *; }
 -keep class android.support.v7.widget.RecyclerView$LayoutManager { *; }
 #----------------------------------------------------------------------------


  #---------------------------------picasso------------------------------------
 -dontwarn com.squareup.picasso.**
  #----------------------------------------------------------------------------

  #---------------------------------ScanKit------------------------------------
  -ignorewarnings
  -keepattributes *Annotation*
  -keepattributes Exceptions
  -keepattributes InnerClasses
  -keepattributes Signature
  -keepattributes SourceFile,LineNumberTable
  -keep class com.hianalytics.android.**{*;}
  -keep class com.huawei.**{*;}

  -keepclasseswithmembers class * {
      public <init>(android.content.Context);
  }
  #PictureSelector 2.0
  -keep class com.luck.picture.lib.** { *; }

  #Ucrop
  -dontwarn com.yalantis.ucrop**
  -keep class com.yalantis.ucrop** { *; }
  -keep interface com.yalantis.ucrop** { *; }

  #Okio
  -dontwarn org.codehaus.mojo.animal_sniffer.*



  #-optimizationpasses 7
  #-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
  -dontoptimize
  -dontusemixedcaseclassnames
  -verbose
  -dontskipnonpubliclibraryclasses
  -dontskipnonpubliclibraryclassmembers
  -dontwarn dalvik.**
  -dontwarn com.tencent.smtt.**
  #-overloadaggressively

  # ------------------ Keep LineNumbers and properties ---------------- #
  -keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
  # --------------------------------------------------------------------------

  # Addidional for x5.sdk classes for apps

  -keep class com.tencent.smtt.export.external.**{
      *;
  }

  -keep class com.tencent.tbs.video.interfaces.IUserStateChangedListener {
  	*;
  }

  -keep class com.tencent.smtt.sdk.CacheManager {
  	public *;
  }

  -keep class com.tencent.smtt.sdk.CookieManager {
  	public *;
  }

  -keep class com.tencent.smtt.sdk.WebHistoryItem {
  	public *;
  }

  -keep class com.tencent.smtt.sdk.WebViewDatabase {
  	public *;
  }

  -keep class com.tencent.smtt.sdk.WebBackForwardList {
  	public *;
  }

  -keep public class com.tencent.smtt.sdk.WebView {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.WebView$HitTestResult {
  	public static final <fields>;
  	public java.lang.String getExtra();
  	public int getType();
  }

  -keep public class com.tencent.smtt.sdk.WebView$WebViewTransport {
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.WebView$PictureListener {
  	public <fields>;
  	public <methods>;
  }


  -keepattributes InnerClasses

  -keep public enum com.tencent.smtt.sdk.WebSettings$** {
      *;
  }

  -keep public enum com.tencent.smtt.sdk.QbSdk$** {
      *;
  }

  -keep public class com.tencent.smtt.sdk.WebSettings {
      public *;
  }


  -keepattributes Signature
  -keep public class com.tencent.smtt.sdk.ValueCallback {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.WebViewClient {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.DownloadListener {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.WebChromeClient {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {
  	public <fields>;
  	public <methods>;
  }

  -keep class com.tencent.smtt.sdk.SystemWebChromeClient{
  	public *;
  }
  # 1. extension interfaces should be apparent
  -keep public class com.tencent.smtt.export.external.extension.interfaces.* {
  	public protected *;
  }

  # 2. interfaces should be apparent
  -keep public class com.tencent.smtt.export.external.interfaces.* {
  	public protected *;
  }

  -keep public class com.tencent.smtt.sdk.WebViewCallbackClient {
  	public protected *;
  }

  -keep public class com.tencent.smtt.sdk.WebStorage$QuotaUpdater {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.WebIconDatabase {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.WebStorage {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.DownloadListener {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.QbSdk {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.QbSdk$PreInitCallback {
  	public <fields>;
  	public <methods>;
  }
  -keep public class com.tencent.smtt.sdk.CookieSyncManager {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.Tbs* {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.utils.LogFileUtils {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.utils.TbsLog {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.utils.TbsLogClient {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.CookieSyncManager {
  	public <fields>;
  	public <methods>;
  }

  # Added for game demos
  -keep public class com.tencent.smtt.sdk.TBSGamePlayer {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.TBSGamePlayerClient* {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.TBSGamePlayerClientExtension {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.TBSGamePlayerService* {
  	public <fields>;
  	public <methods>;
  }

  -keep public class com.tencent.smtt.utils.Apn {
  	public <fields>;
  	public <methods>;
  }
  -keep class com.tencent.smtt.** {
  	*;
  }
  # end


  -keep public class com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension {
  	public <fields>;
  	public <methods>;
  }

  -keep class MTT.ThirdAppInfoNew {
  	*;
  }

  -keep class com.tencent.mtt.MttTraceEvent {
  	*;
  }

  # Game related
  -keep public class com.tencent.smtt.gamesdk.* {
  	public protected *;
  }

  -keep public class com.tencent.smtt.sdk.TBSGameBooter {
          public <fields>;
          public <methods>;
  }

  -keep public class com.tencent.smtt.sdk.TBSGameBaseActivity {
  	public protected *;
  }

  -keep public class com.tencent.smtt.sdk.TBSGameBaseActivityProxy {
  	public protected *;
  }

  -keep public class com.tencent.smtt.gamesdk.internal.TBSGameServiceClient {
  	public *;
  }
  -keep class com.baidu.** {*;}
  -keep class mapsdkvi.com.** {*;}
  -dontwarn com.baidu.**

  -keep class com.baidu.speech.**{*;}
  -keep class com.baidu.tts.**{*;}
  -keep class com.baidu.speechsynthesizer.**{*;}
  #---------------------------------------------------------------------------


  #------------------  下方是android平台自带的排除项，这里不要动         ----------------


  -keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
  }

  -keepclasseswithmembers class * {
  	public <init>(android.content.Context, android.util.AttributeSet);
  }

  -keepclasseswithmembers class * {
  	public <init>(android.content.Context, android.util.AttributeSet, int);
  }

  -keepattributes *Annotation*

  -keepclasseswithmembernames class *{
  	native <methods>;
  }

  -keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
  }

  #------------------  下方是共性的排除项目         ----------------
  # 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
  # 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除

  -keepclasseswithmembers class * {
      ... *JNI*(...);
  }

  -keepclasseswithmembernames class * {
  	... *JRI*(...);
  }

  -keep class **JNI* {*;}

  -keep class com.gavindon.mvvm_lib.** {*; }

  -keep public class * implements com.bumptech.glide.module.GlideModule
  -keep public class * extends com.bumptech.glide.module.AppGlideModule
  -keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
  }
  -dontwarn com.tencent.bugly.**
  -keep public class com.tencent.bugly.**{*;}
  # tinker混淆规则
  -dontwarn com.tencent.tinker.**
  -keep class com.tencent.tinker.** { *; }

  #  greendao------------------
  -keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
  public static java.lang.String TABLENAME;
  }
  -keep class **$Properties { *; }

  # If you DO use SQLCipher:
  -keep class org.greenrobot.greendao.database.SqlCipherEncryptedHelper { *; }

  # If you do NOT use SQLCipher:
  -dontwarn net.sqlcipher.database.**
  # If you do NOT use RxJava:
  -dontwarn rx.**
  -keep class androidx.recyclerview.widget.**{*;}
  -keep class androidx.viewpager2.widget.**{*;}
  #极光
  -dontoptimize
  -dontpreverify

  -dontwarn cn.jpush.**
  -keep class cn.jpush.** { *; }
  -keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

  -dontwarn cn.jiguang.**
  -keep class cn.jiguang.** { *; }
  -keep class com.stxx.wyhvisitorandroid.view.asr.**{*;}
   #AR科普
  -keep class bitter.jnibridge.* { *; }
  -keep class com.unity3d.player.* { *; }
  -keep class org.fmod.* { *; }














