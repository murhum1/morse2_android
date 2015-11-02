#include <string.h>
#include <jni.h>

extern "C" {

    JNIEXPORT jstring JNICALL Java_morse_morseapp_MainActivity_returnedString(JNIEnv* env, jobject thiz ) {
        return env->NewStringUTF("asdadasdsad");
    }

    JNIEXPORT jintArray JNICALL Java_morse_morseapp_CameraAndFlashHandler_getLights(JNIEnv* env, jobject thiz, jbyteArray luminance, jint width, jint height) {
        jintArray intit = env->NewIntArray(4);
        jint *intElements = env->GetIntArrayElements(intit, NULL);
        intElements[0] = 1;
        intElements[1] = 2;
        intElements[2] = 3;
        intElements[3] = 4;
        env->ReleaseIntArrayElements(intit, intElements, NULL);
        return intit;
    }

}