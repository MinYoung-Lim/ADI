package com.adoublei.pbl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.minyoung.ndklib.NativeWrapper;

public class ScanActivity6 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan6);

        NativeWrapper nativeApi = new NativeWrapper();
        int result = nativeApi.nativeSum(5, 10);

        String s = String.valueOf(result);

        Log.e("5+10=", s);

    }
}