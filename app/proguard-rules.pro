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
# ====================================================================
# CONFIGURAÇÃO GERAL E DEBUG
# ====================================================================

# Manter a informação da linha e do arquivo fonte para facilitar o debug de crashes
-keepattributes SourceFile,LineNumberTable
# Manter o atributo de assinatura genérica. VITAL para desserialização (List<T>, etc.)
-keepattributes Signature
# Manter todas as anotações, cruciais para Hilt, Dagger, Retrofit e Serialização
-keepattributes *Annotation*
# Não remover classes internas e anônimas (incluindo Lambdas de Coroutines/UI/Callbacks)
-keepnames class **$* { *; }


# ====================================================================
# PROTEÇÃO DO SEU CÓDIGO (Pacote principal: com.scherzolambda.horarios)
# ====================================================================

# 1. Manter a classe Application, ViewModels, Activities e Fragments
# Estas são as portas de entrada para o Hilt.
#-keep class com.scherzolambda.horarios.**Activity { *; }
#-keep class com.scherzolambda.horarios.**Fragment { *; }
-keep class com.scherzolambda.horarios.**ViewModel { *; }
-keep class com.scherzolambda.horarios.HorariosApplication { *; }

# 2. Manter Repositórios/Classes Injetáveis (Se houver ClassCastException aqui)
# Esta regra é genérica para cobrir qualquer classe no seu app que use injeção/reflexão.
#-keep class com.scherzolambda.horarios.** { *; }

# 3. Manter as Classes de Modelo de Dados (DTOs)
# Garante que todos os campos, construtores e métodos sejam mantidos para a serialização.
-keep class com.scherzolambda.horarios.data_transformation.models.** {
    <fields>;
    <init>(...);
    *;
}
-keep class com.scherzolambda.horarios.data_transformation.api.models.** {
    <fields>;
    <init>(...);
    *;
}


# ====================================================================
# REGRAS PARA DEPENDÊNCIAS (Hilt, Retrofit, Serialização)
# ====================================================================

# HILT / DAGGER
# As regras de Hilt precisam ser bem explícitas:
-keep class * extends dagger.internal.BaseInjectableModule { *; }
-keep class * extends dagger.internal.BaseComponent { *; }

# Manter os nomes das classes geradas pelo Hilt para Injeção
-keepnames class com.scherzolambda.horarios.**.**_Factory
-keepnames class com.scherzolambda.horarios.**.**_MembersInjector
-keepnames class com.scherzolambda.horarios.**.**_Provide**Factory
-keepnames class com.scherzolambda.horarios.**.**_HiltModule
-keepnames class com.scherzolambda.horarios.**.**_Component

# Manter métodos e campos anotados com @Inject e @Provides
-keepclasseswithmembers class * { @javax.inject.Inject <fields>; }
-keepclasseswithmembers class * { @javax.inject.Inject <init>(...); }
-keepclasseswithmembers class * { @dagger.Provides * *(...); }
-keepclasseswithmembers class * { @dagger.Binds * *(...); }


# GSON (Conversor de Retrofit)
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }


# KOTLINX SERIALIZATION
#-keep class kotlinx.serialization.** { *; }
# Regra para preservar os métodos de companion objects de data classes, essenciais para kotlinx-serialization
-keepclassmembers class **$Companion {
    <methods>;
}


# RETROFIT E COROUTINES
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
# Previne warnings do OkHttp (usado pelo Retrofit)
-dontwarn okio.**
-dontwarn javax.annotation.**