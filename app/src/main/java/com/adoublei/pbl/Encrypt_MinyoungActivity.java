package com.adoublei.pbl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encrypt_MinyoungActivity extends AppCompatActivity {

    private Button btn_send;
    private String text;
    public String encryptText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt__minyoung);

        btn_send = findViewById(R.id.btn_send);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                text = "이것은 비밀 메시지";
                try {
                    encryptText2 = AES256Chiper.Encrypt(text, "key");
                    Log.e("암호화메시지", encryptText2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference conditionRef = mRootRef.child("암호화메시지3");
                conditionRef.setValue(encryptText2);
            }
        });




    }
}