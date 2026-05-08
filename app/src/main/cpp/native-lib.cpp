#include <jni.h>
#include <string>
#include <algorithm>
#include <climits>
#include <android/log.h>

#define LAB_TAG "LAB22_NATIVE_BRIDGE"
#define LOG_INFO(...) __android_log_print(ANDROID_LOG_INFO, LAB_TAG, __VA_ARGS__)
#define LOG_ERROR(...) __android_log_print(ANDROID_LOG_ERROR, LAB_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jstring JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeGreeting(
        JNIEnv *env,
        jobject /* activity */) {

    LOG_INFO("nativeGreeting() appelee depuis Java");

    std::string message = "Connexion active : Java communique avec C++ via JNI.";
    return env->NewStringUTF(message.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeFactorialSafe(
        JNIEnv *env,
        jobject /* activity */,
        jint number) {

    if (number < 0) {
        LOG_ERROR("Erreur factoriel : valeur negative = %d", number);
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

    LOG_INFO("Factoriel calcule en natif : %d! = %lld", number, result);
    return static_cast<jint>(result);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ma_ensa_mobile_jnibridge_MainActivity_nativeMirrorText(
        JNIEnv *env,
        jobject /* activity */,
        jstring inputText) {

    if (inputText == nullptr) {
        LOG_ERROR("Texte recu null");
        return env->NewStringUTF("Erreur : texte null");
    }

    const char *rawText = env->GetStringUTFChars(inputText, nullptr);

    if (rawText == nullptr) {
        LOG_ERROR("Impossible de convertir la String Java");
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
        LOG_ERROR("Tableau recu null");
        return -1;
    }

    jsize size = env->GetArrayLength(valuesArray);
    jint *items = env->GetIntArrayElements(valuesArray, nullptr);

    if (items == nullptr) {
        LOG_ERROR("Impossible d'acceder au tableau Java");
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

    LOG_INFO("Somme calculee en natif = %lld", total);

    return static_cast<jint>(total);
}