package com.adoublei.pbl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OpenCV_MainActivity2 extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private ProgressBar bar;
    private static final int REQUEST_FILE = 10;
    private static final int PERMISSION_FILE = 20;
    private static final int REQUEST_CAMERA = 30;
    private static final int PERMISSION_CAMERA = 40;
    private final int GET_GALLERY_IMAGE = 200;


    String currentPhotoPath;

    FirebaseAutoMLRemoteModel remoteModel =
            new FirebaseAutoMLRemoteModel.Builder("sample2").build();
       //FirebaseAutoMLLocalModel localModel = new FirebaseAutoMLLocalModel.Builder()
        //.setAssetFilePath("model/manifest.json").build();
    FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
            .requireWifi()
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_c_v__main2);

        Button buttonA = findViewById(R.id.button_album);
        Button buttonC = findViewById(R.id.button_capture);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.textView);
        bar = findViewById(R.id.loading);

        bar.setVisibility(View.INVISIBLE);

        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(OpenCV_MainActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OpenCV_MainActivity2.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FILE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_FILE);
                }
            }
        });

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(OpenCV_MainActivity2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OpenCV_MainActivity2.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                } else {
                    dispatchTakePictureIntent();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri;
            switch (requestCode) {
                case REQUEST_FILE:
                    uri = data.getData();
                    FirebaseModelManager.getInstance().download(remoteModel, conditions)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e("remoteModel다운로드", "성공");
                                }
                            });
                    setLabelerFromRemoteModel(uri);
                    textView.setText("");
                    imageView.setImageURI(uri);
                    break;
                case REQUEST_CAMERA:
                    File imgFile = new File(currentPhotoPath);
                    uri = Uri.fromFile(imgFile);
                    FirebaseModelManager.getInstance().download(remoteModel, conditions);
                    setLabelerFromRemoteModel(uri);
                    textView.setText("");
                    imageView.setImageURI(uri);
                    break;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 인텐트를 처리하기 위한 카메라 액티비티가 있는지 확인 (Ensure that there's a camera activity to handle the intent)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // photo 가 들어갈 임시 파일 생성
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) { // photoFile 이 성공적으로 만들어졌으면 진행
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.adoublei.pbl.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private void setLabelerFromRemoteModel(Uri uri) {
        bar.setVisibility(View.VISIBLE);
        FirebaseVisionImageLabeler labeler;
        try {
            FirebaseVisionOnDeviceAutoMLImageLabelerOptions options =
                    new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(remoteModel)
                            .setConfidenceThreshold(0.0f)
                            .build();
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(OpenCV_MainActivity2.this, uri);
            processImageLabeler(labeler, image);
        } catch (FirebaseMLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void processImageLabeler(FirebaseVisionImageLabeler labeler, FirebaseVisionImage image) {
        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override // 이미지 라벨링 성공
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        bar.setVisibility(View.INVISIBLE);
                        for (FirebaseVisionImageLabel label : labels) {
                            String eachLabel = label.getText().toUpperCase();
                            float confidence = label.getConfidence();
                            textView.append(eachLabel + " : " + ("" + confidence * 100).subSequence(0, 4) + "%" + "\n");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override // 이미지 라벨링 실패
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OpenCV_MainActivity2.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}