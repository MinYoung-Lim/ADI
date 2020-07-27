package com.adoublei.pbl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Opencv_MainActivity extends AppCompatActivity {

    public float confidence;
    public FirebaseVisionImage image;
    private final int GET_GALLERY_IMAGE = 200;
    ImageView gallery_img;
    Button gallery_btn;
    Button btn_mlkit;
    TextView tv_label;
    public FirebaseVisionOnDeviceAutoMLImageLabelerOptions options;
    public FirebaseVisionImageLabeler labeler;
    public FirebaseAutoMLLocalModel localModel;
    public FirebaseAutoMLRemoteModel remoteModel;
    public FirebaseModelDownloadConditions conditions;
    public String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv__main);

        gallery_btn = findViewById(R.id.btn_gallery); //gallery button
        gallery_img = findViewById(R.id.img_gallery); //imageView
        tv_label = findViewById(R.id.tv_label);

        /*btn_mlkit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MLkitActivity2.class);
                startActivity(intent);
            }
        });*/

        gallery_btn.setOnClickListener(new View.OnClickListener(){ // gallery button 클릭 시 album으로 이동
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) { //album에서 사진 선택 및 가져오기 성공하면 requestCode가 1
            Uri selectedImageUri = data.getData();
            gallery_img.setImageURI(selectedImageUri);

            try {

                    Log.e("onActivityResult", "실행됨");

                    /*InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    gallery_img.setImageBitmap(img); //gallery_img에 사진 업로드*/


                FirebaseApp.initializeApp(getApplicationContext());

                    MakeRemoteModel();
                    MakeLocalModel();
                    MakeLabeler();

                    // 입력 이미지 준비
                    try {
                        Log.e("입력 이미지 준비", "실행됨");
                        image = FirebaseVisionImage.fromFilePath(getApplicationContext(), selectedImageUri);
                    } catch (IOException e) {
                        Log.e("입력이미지준비", "실행안됨");
                        e.printStackTrace();
                    }

                    // 이미지 레이블러 실행
                    labeler.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                    Log.e("이미지레이블러실행","실행됨");
                                    for (FirebaseVisionImageLabel label: labels) {
                                        Log.e("Opencv_MainActivity", String.valueOf(labels));
                                        text = label.getText();
                                        confidence = label.getConfidence();
                                    }

                                    tv_label.setText((int) confidence);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("이미지레이블러실행","실행안됨");
                                    e.printStackTrace();
                                }
                            });



                } catch (Exception e) { //오류 처리
                    e.printStackTrace();
                }

        }
    }

    public void MakeRemoteModel(){

        //Firebase 호스팅(원격) 모델 소스 구성
        remoteModel = new FirebaseAutoMLRemoteModel.Builder("sample2").build();
        conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Success.
                        Log.e("원격 모델 소스 구성", "성공");
                    }
                });

    }

    public void MakeLocalModel(){

        // 로컬 모델 소스 구성
        localModel = new FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("manifest.json")
                .build();


    }

    public void MakeLabeler(){

        // firebase 초기화
        //FirebaseApp.initializeApp(getApplicationContext());


        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isDownloaded) {
                        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder optionsBuilder;
                        if (isDownloaded) {
                            optionsBuilder = new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(remoteModel);
                        } else {
                            optionsBuilder = new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel);
                        }
                        FirebaseVisionOnDeviceAutoMLImageLabelerOptions options = optionsBuilder
                                .setConfidenceThreshold(0.0f)  // Evaluate your model in the Firebase console
                                // to determine an appropriate threshold.
                                .build();

                        try {
                            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
                        } catch (FirebaseMLException e) {
                            // Error.
                        }
                    }
                });
    }

}