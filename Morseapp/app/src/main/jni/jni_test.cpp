#include <string.h>
#include <jni.h>

extern "C" {

    JNIEXPORT jstring JNICALL Java_morse_morseapp_MainActivity_returnedString(JNIEnv* env, jobject thiz ) {
        return env->NewStringUTF("asdadasdsad");
    }

}