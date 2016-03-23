# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/mashnoor/adt-bundle-linux-x86_64-20140321/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

-keep class org.apache.http.**{
                                 *;
                              }
-dontwarn com.facebook.**
-dontwarn org.apache.**
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.**
-keep interface org.apache.http.**
-dontwarn android.net.**
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
