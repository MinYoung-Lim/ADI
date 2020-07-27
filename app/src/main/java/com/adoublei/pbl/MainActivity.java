package com.adoublei.pbl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_opencv;
    Button btn_encrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_opencv = findViewById(R.id.btn_opencv);
        btn_encrypt = findViewById(R.id.btn_encrypt);

        btn_opencv.setOnClickListener(new View.OnClickListener() {  // OpenCV화면으로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OpenCV_MainActivity3.class);
                startActivity(intent);
            }
        });

        btn_encrypt.setOnClickListener(new View.OnClickListener() {  // 암호화화면으로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Encrypt_MainActivity.class);
                startActivity(intent);
            }
        });


    }
}