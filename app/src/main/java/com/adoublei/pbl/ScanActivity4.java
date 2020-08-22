package com.adoublei.pbl;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;



public class ScanActivity4 extends AppCompatActivity {

    private TextView txt_result;



    static {

        System.loadLibrary("Calculator");

    }



    public native int getSum(int num1, int num2);



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        txt_result = (TextView) findViewById(R.id.txt_result);



        int num1 = 10;

        int num2 = 20;



        int sum = getSum(num1, num2);

        txt_result.setText("JNI Sample :: 합계 : " + sum);

    }

}