#include <string.h>
#include <jni.h>
#include <vector>
#include "ImageProcessor.hpp"
extern "C" {
    using namespace FW;
    ImageProcessor ip;

    JNIEXPORT jintArray JNICALL Java_morse_morseapp_MainActivity_getLights(JNIEnv* env, jobject thiz, jbyteArray luminance, jint width, jint height) {

        char* data = (char*)env->GetByteArrayElements(luminance, NULL);
        ip.processImage(data, Vec2i(width, height));

        int count = ip.blinkers.size();
        jintArray intit = env->NewIntArray(count*5);
        jint *intElements = env->GetIntArrayElements(intit, NULL);

        //memcpy(intElements, &ip.blinkers[0], count*5*sizeof(int));
        for(int i = 0; i < count; ++i) {
            blinker b = ip.blinkers[i];
            intElements[i*5+0] = b.pos.x;
            intElements[i*5+1] = b.pos.y;
            intElements[i*5+2] = b.brightness;
            intElements[i*5+3] = b.size;
            intElements[i*5+4] = b.ID;
        }
        env->ReleaseIntArrayElements(intit, intElements, NULL);
        env->ReleaseByteArrayElements(luminance, (jbyte*)data, NULL);
        return intit;
    }
}