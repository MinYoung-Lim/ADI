package com.adoublei.pbl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;

public class Opencv_MainActivity extends AppCompatActivity {

    ImageView gallery_img;
    Button gallery_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv__main);

        gallery_btn = findViewById(R.id.btn_gallery); //gallery button
        gallery_img = findViewById(R.id.img_gallery); //imageView

        gallery_btn.setOnClickListener(new View.OnClickListener(){ // gallery button 클릭 시 album으로 이동
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { //album에서 사진 선택 및 가져오기 성공하면 requestCode가 1
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    gallery_img.setImageBitmap(img); //gallery_img에 사진 업로드
                } catch (Exception e) { //오류 처리
                    e.printStackTrace();
                }
            }
        }
    }
}