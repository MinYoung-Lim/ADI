package com.adoublei.pbl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;

import static com.adoublei.pbl.OpenCV_MainActivity3.selectedBitmap;

public class ScanActivity3 extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getClass().getSimpleName();
    private boolean isOpenCvLoaded = false;
    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageView;
    private Mat edged;
    private Random rng = new Random(12345);
    private double maxVal;
    private int maxValIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan3);



        imageView = findViewById(R.id.imageView);
        final ImageView imageView2 = findViewById(R.id.imageView2);

        Button btn_opencv = findViewById(R.id.btn_opencv);
        Button btn_gallery = findViewById(R.id.btn_gallery);

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        btn_opencv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !isOpenCvLoaded )
                    return;

                //InputStream is = getAssets().open("redshoes.jpg");
                Bitmap bitmap = selectedBitmap;
                imageView.setImageBitmap(bitmap);

                Mat img = new Mat();
                Utils.bitmapToMat(bitmap, img);

                scan(img);

                /*Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2GRAY);

                Bitmap grayBitmap = Bitmap.createBitmap(img.cols(), img.rows(), null);
                Utils.matToBitmap(img, grayBitmap);

                imageView2.setImageBitmap(grayBitmap);*/

            }
        });
    }

    private void scan(Mat img) {

        // 그레이스케일 변환 및 캐니 엣지
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(img, img, new Size(3,3), 0);
        Imgproc.Canny(img, edged, 75,200);

       // List<MatOfPoint> contours = new Array;
        //Mat hierarchy = new Mat();
        //Imgproc.findContours(edged, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

       /* Mat drawing = Mat.zeros(edged.size(), CvType.CV_8UC3);
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            Imgproc.drawContours(drawing, contours, i, color, 2, 0, hierarchy, 0, new Point());
            maxVal = new MatOfPoint2f(contours.get(i).toArray());

        }
*/
       /* maxValIdx = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal < contourArea)
            {
                maxVal = contourArea;
                maxValIdx = contourIdx;
            }
        }

        double pts = Imgproc.arcLength(maxVal, true);*/


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) { //album에서 사진 선택 및 가져오기 성공하면 requestCode가 1
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);

            // Uri를 Bitmap으로 변환
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(selectedImageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            selectedBitmap = BitmapFactory.decodeStream(inputStream);


        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            isOpenCvLoaded = true;
        }
    }
}