//
// Created by minyo on 2020-08-29.
//
#include <jni.h>
#include "com_adoublei_pbl_ScanActivity7.h"

#include <opencv2/opencv.hpp>

using namespace cv;

extern "C" JNIEXPORT void JNICALL Java_com_adoublei_pbl_ScanActivity7_ConvertRGBtoGray
  (JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult){

       Mat &matInput = *(Mat *)matAddrInput;
       Mat &matResult = *(Mat *)matAddrResult;

       cvtColor(matInput, matResult, COLOR_RGBA2GRAY);

  }