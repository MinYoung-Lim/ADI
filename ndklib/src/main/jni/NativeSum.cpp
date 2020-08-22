//
// Created by minyo on 2020-08-22.
//

#include "com_minyoung_ndklib_NativeWrapper.h"

JNIEXPORT jint JNICALL Java_com_minyoung_ndklib_NativeWrapper_nativeSum
        (JNIEnv *env, jobject obj, jint a, jint b)
{
    return a+b;
}


