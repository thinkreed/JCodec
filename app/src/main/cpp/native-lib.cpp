#include <jni.h>
#include <string>
#include "pcm_test.h"

extern "C"
JNIEXPORT jstring

JNICALL
Java_thinkreed_jcodec_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
