#include <jni.h>
#include <string>
#include <algorithm>
#include <climits>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <cerrno>
#include <cctype>
#include <android/log.h>
#include <unistd.h>

#define GUARD_TAG "LAB23_NATIVE_GUARD"
#define LOG_INFO(...) __android_log_print(ANDROID_LOG_INFO, GUARD_TAG, __VA_ARGS__)
#define LOG_WARN(...) __android_log_print(ANDROID_LOG_WARN, GUARD_TAG, __VA_ARGS__)
#define LOG_ERROR(...) __android_log_print(ANDROID_LOG_ERROR, GUARD_TAG, __VA_ARGS__)


static std::string toLowerCopy(const char *text) {
    std::string value(text);

    std::transform(value.begin(), value.end(), value.begin(),
                   [](unsigned char c) {
                       return static_cast<char>(std::tolower(c));
                   });

    return value;
}

// --------------------------------------------------
// Signal 1 : détection stable via TracerPid
// --------------------------------------------------
static bool detectDebugByTracerPid() {
    FILE *statusFile = fopen("/proc/self/status", "r");

    if (!statusFile) {
        LOG_WARN("Impossible d'ouvrir /proc/self/status");
        return false;
    }

    char line[256];

    while (fgets(line, sizeof(line), statusFile)) {
        if (strncmp(line, "TracerPid:", 10) == 0) {
            int tracerPid = atoi(line + 10);
            fclose(statusFile);

            if (tracerPid > 0) {
                LOG_ERROR("Debug detecte via TracerPid : %d", tracerPid);
                return true;
            }

            LOG_INFO("TracerPid = 0 : aucun debugger attache");
            return false;
        }
    }

    fclose(statusFile);
    LOG_WARN("TracerPid introuvable dans /proc/self/status");
    return false;
}

// --------------------------------------------------
// Signal 2 : inspection simple de /proc/self/maps
// --------------------------------------------------
static bool detectSuspiciousLoadedLibraries() {
    FILE *mapsFile = fopen("/proc/self/maps", "r");

    if (!mapsFile) {
        LOG_WARN("Impossible d'ouvrir /proc/self/maps");
        return false;
    }

    char line[512];

    while (fgets(line, sizeof(line), mapsFile)) {
        std::string loweredLine = toLowerCopy(line);

        if (loweredLine.find("frida") != std::string::npos ||
            loweredLine.find("xposed") != std::string::npos ||
            loweredLine.find("substrate") != std::string::npos ||
            loweredLine.find("gdbserver") != std::string::npos ||
            loweredLine.find("libgdb") != std::string::npos ||
            loweredLine.find("lldb") != std::string::npos ||
            loweredLine.find("magisk") != std::string::npos ||
            loweredLine.find("zygisk") != std::string::npos) {

            LOG_ERROR("Signature suspecte detectee dans maps : %s", line);
            fclose(mapsFile);
            return true;
        }
    }

    fclose(mapsFile);
    LOG_INFO("Inspection maps : aucune signature suspecte detectee");
    return false;
}

// --------------------------------------------------
// Code d'état global
// 0 = OK
// 1 = trace/debug detecte
// 2 = bibliotheque suspecte detectee
// 3 = plusieurs signaux detectes
// --------------------------------------------------
static int buildSecurityStateCode() {
    bool debugSignal = detectDebugByTracerPid();
    bool mapsSignal = detectSuspiciousLoadedLibraries();

    if (debugSignal && mapsSignal) {
        LOG_ERROR("Etat securite : plusieurs signaux suspects detectes");
        return 3;
    }

    if (debugSignal) {
        LOG_ERROR("Etat securite : trace/debug detecte");
        return 1;
    }

    if (mapsSignal) {
        LOG_ERROR("Etat securite : bibliotheque suspecte detectee");
        return 2;
    }

    LOG_INFO("Etat securite : environnement OK");
    return 0;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_isDebugDetected(
        JNIEnv *env,
        jobject /* activity */) {

    int securityCode = buildSecurityStateCode();
    return securityCode == 0 ? JNI_FALSE : JNI_TRUE;
}

extern "C"
JNIEXPORT jint JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeSecurityCode(
        JNIEnv *env,
        jobject /* activity */) {

    return static_cast<jint>(buildSecurityStateCode());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeSecuritySummary(
        JNIEnv *env,
        jobject /* activity */) {

    int code = buildSecurityStateCode();

    switch (code) {
        case 0:
            return env->NewStringUTF("Aucun signal suspect : environnement d'execution normal.");
        case 1:
            return env->NewStringUTF("Signal trace/debug detecte : mode restreint active.");
        case 2:
            return env->NewStringUTF("Bibliotheque ou outil d'instrumentation suspect detecte dans maps.");
        case 3:
            return env->NewStringUTF("Plusieurs signaux suspects detectes : debug/instrumentation probable.");
        default:
            return env->NewStringUTF("Etat de securite inconnu.");
    }
}

// --------------------------------------------------
// Fonctions JNI du lab précédent
// --------------------------------------------------

extern "C"
JNIEXPORT jstring JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeGreeting(
        JNIEnv *env,
        jobject /* activity */) {

    LOG_INFO("nativeGreeting() appelee");
    return env->NewStringUTF("Zone native active : C++ repond via JNI.");
}

extern "C"
JNIEXPORT jint JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeFactorialSafe(
        JNIEnv *env,
        jobject /* activity */,
        jint number) {

    if (number < 0) {
        LOG_ERROR("Factoriel refuse : valeur negative = %d", number);
        return -1;
    }

    long long result = 1;

    for (int i = 1; i <= number; i++) {
        result *= i;

        if (result > INT_MAX) {
            LOG_ERROR("Overflow detecte pour factorial(%d)", number);
            return -2;
        }
    }

    LOG_INFO("Factoriel natif calcule : %d! = %lld", number, result);
    return static_cast<jint>(result);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeMirrorText(
        JNIEnv *env,
        jobject /* activity */,
        jstring inputText) {

    if (inputText == nullptr) {
        LOG_ERROR("Texte null recu");
        return env->NewStringUTF("Erreur : texte null");
    }

    const char *rawText = env->GetStringUTFChars(inputText, nullptr);

    if (rawText == nullptr) {
        LOG_ERROR("Conversion String Java impossible");
        return env->NewStringUTF("Erreur conversion JNI");
    }

    std::string cppText(rawText);
    env->ReleaseStringUTFChars(inputText, rawText);

    std::reverse(cppText.begin(), cppText.end());

    LOG_INFO("Texte inverse en natif : %s", cppText.c_str());
    return env->NewStringUTF(cppText.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeSumValues(
        JNIEnv *env,
        jobject /* activity */,
        jintArray valuesArray) {

    if (valuesArray == nullptr) {
        LOG_ERROR("Tableau null recu");
        return -1;
    }

    jsize size = env->GetArrayLength(valuesArray);
    jint *items = env->GetIntArrayElements(valuesArray, nullptr);

    if (items == nullptr) {
        LOG_ERROR("Acces au tableau Java impossible");
        return -2;
    }

    long long total = 0;

    for (jsize i = 0; i < size; i++) {
        total += items[i];
    }

    env->ReleaseIntArrayElements(valuesArray, items, 0);

    if (total > INT_MAX) {
        LOG_ERROR("Overflow pendant la somme du tableau");
        return -3;
    }

    LOG_INFO("Somme native calculee = %lld", total);
    return static_cast<jint>(total);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeBenchmarkLoop(
        JNIEnv *env,
        jobject /* activity */,
        jint rounds) {

    if (rounds <= 0) {
        LOG_ERROR("Benchmark refuse : iterations invalides = %d", rounds);
        return -1;
    }

    long long total = 0;

    for (int i = 1; i <= rounds; i++) {
        total += i;
    }

    LOG_INFO("Benchmark natif termine : somme 1..%d = %lld", rounds, total);
    return static_cast<jlong>(total);
}