package com.adoublei.pbl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


public class UploadMainActivity extends AppCompatActivity {

    Button btn_upload;
    ImageView iv_bitmap;

    private String EncryptImg="";
    private String DecryptImg="";
    private String bitmapToString="";
    private Bitmap stringToBitmap;
    private String text;
    public String encryptText2;
    private Uri filePath;
    private static final String charsetName = "UTF-8";
    private String userEamil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_main);

        btn_upload = findViewById(R.id.btn_upload);
        iv_bitmap = findViewById(R.id.iv_bitmap);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

    }

    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            try {

                Bitmap ImgBitmap = null;

                ImgBitmap = resize(getApplicationContext(), filePath, 1000);

                bitmapToString = BitmapToString(ImgBitmap);
                ImgBitmap.recycle();

                // 키 생성 (추후에 작성해야함)


                // 키 설정 ( 이부분도 나중에 키생성 어떻게할지 정해서 다시 코딩해야함)
                final String secretKey = "love";
                String originalString = bitmapToString;

                // 이미지 암호화
                EncryptImg = Cryptor.encrypt(originalString, secretKey);
                Log.e("암호화된이미지", EncryptImg);



                // 사용자의 이메일 아래에 넣는 코드 (임시로... 추후에 로그인 구현된 후, 다시 작성해야 함)
                userEamil = "minyounge17";
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference email = mRootRef.child("User1");
                email.child("email").setValue(userEamil);



                // 암호화된 이미지 업로드
                DatabaseReference conditionRef = mRootRef.child("User1").child("album");
                conditionRef.child("EncryptImg").setValue(EncryptImg);


                // 키 업로드 (임시로.. 나중에는 키관리따로해야함)
                DatabaseReference key = mRootRef.child("User1").child("album");
                key.child("key").setValue(secretKey);


                // 복호화
                /*
                DecryptImg = Cryptor.decrypt(EncryptImg, secretKey);
                stringToBitmap = StringToBitmap(DecryptImg);
                iv_bitmap.setImageBitmap(stringToBitmap);

                Log.e("복호화된이미지", DecryptImg);


                // 복호화된 이미지 업로드
                DatabaseReference mRootRef2 = FirebaseDatabase.getInstance().getReference();
                DatabaseReference conditionRef2 = mRootRef2.child("복호화된 이미지5");
                conditionRef2.setValue(DecryptImg);
                 */

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



    /*
     * String형을 BitMap으로 변환시켜주는 함수
     * */
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    /*
     * Bitmap을 String형으로 변환
     * */
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 15, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }

    /*
     * Bitmap을 byte배열로 변환
     * */
    public static byte[] BitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return baos.toByteArray();
    }

    /** * String 객체를 압축하여 String 으로 리턴한다. * @param string * @return */
    public synchronized static String compressString(String string)
    { return byteToString(compress(string)); }

    /** * 압축된 스트링을 복귀시켜서 String 으로 리턴한다. * @param compressed * @return */
    public synchronized static String decompressString(String compressed) {
        return decompress(hexToByteArray(compressed)); }

    private static String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        try {
            for (byte b : bytes) { sb.append(String.format("%02X", b)); }
        } catch (Exception e) { e.printStackTrace();
            return null;
        } return sb.toString();
    }

    private static byte[] compress(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try { OutputStream out = new DeflaterOutputStream(baos);
            out.write(text.getBytes(charsetName));
            out.close();
        } catch (UnsupportedEncodingException e) { e.printStackTrace();
            return null;
        } catch (IOException e) { e.printStackTrace();
            return null;
        } return baos.toByteArray();
    }

    private static String decompress(byte[] bytes) {
        InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try { byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) baos.write(buffer, 0, len);
            return new String(baos.toByteArray(), charsetName);
        } catch (IOException e) { e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    /** * 16진 문자열을 byte 배열로 변환한다. */
    private static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[]{};
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        } return bytes;
    }

    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }

}