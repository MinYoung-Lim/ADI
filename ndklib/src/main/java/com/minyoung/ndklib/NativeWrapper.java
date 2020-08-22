package com.minyoung.ndklib;

public class NativeWrapper {
    static {
        System.loadLibrary("ndklib");

    }
        public native int nativeSum(int a, int b);
    }

