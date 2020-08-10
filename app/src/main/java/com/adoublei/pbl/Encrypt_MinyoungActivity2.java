package com.adoublei.pbl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class Encrypt_MinyoungActivity2 extends AppCompatActivity {

    private Button btn_send;
    private Button btn_gallery;
    private ImageView iv_bitmap;
    private String text;
    public String encryptText2;
    private Uri filePath;

    private String EncryptImg="";
    private String DecryptImg="";
    private String bitmapToString="";
    private Bitmap stringToBitmap;
    private static final String charsetName = "UTF-8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt__minyoung2);

        btn_send = findViewById(R.id.btn_send);
        btn_gallery = findViewById(R.id.btn_gallery);
        iv_bitmap = findViewById(R.id.iv_bitmap);


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


        btn_gallery.setOnClickListener(new View.OnClickListener() {
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

                // Uri파일로 bitmap resize
                //resize(getApplicationContext(), filePath, 1000);

                Bitmap ImgBitmap = null;

                //ImgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImgBitmap = resize(getApplicationContext(), filePath, 1000);

                bitmapToString = BitmapToString(ImgBitmap);
                ImgBitmap.recycle();

                // 키 설정
                final String secretKey = "love";
                String originalString = bitmapToString;

                // 암호화 체크
                /*String testEncrypt = Cryptor.encrypt(originalString, secretKey);
                DatabaseReference mRootRef5 = FirebaseDatabase.getInstance().getReference();
                DatabaseReference conditionRef5 = mRootRef5.child("암호문");
                conditionRef5.setValue(testEncrypt);
                Log.e("암호문", testEncrypt);*/

                // 이미지 암호화
                EncryptImg = Cryptor.encrypt(originalString, secretKey);
                Log.e("암호화된이미지", EncryptImg);

                // 암호화된 이미지 업로드
                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference conditionRef = mRootRef.child("암호화된 이미지6");
                conditionRef.setValue(EncryptImg);

                // 복호화
                DecryptImg = Cryptor.decrypt(EncryptImg, secretKey);
                stringToBitmap = StringToBitmap(DecryptImg);
                iv_bitmap.setImageBitmap(stringToBitmap);

                Log.e("복호화된이미지", DecryptImg);


                // 복호화된 이미지 업로드
                DatabaseReference mRootRef2 = FirebaseDatabase.getInstance().getReference();
                DatabaseReference conditionRef2 = mRootRef2.child("복호화된 이미지5");
                conditionRef2.setValue(DecryptImg);

                // 복호화 체크
                /*String testDecrypt = Cryptor.decrypt(testEncrypt, secretKey);
                DatabaseReference mRootRef6 = FirebaseDatabase.getInstance().getReference();
                DatabaseReference conditionRef6 = mRootRef6.child("복호문");
                conditionRef6.setValue(testDecrypt);
                Log.e("복호문", testDecrypt);*/

                // DecryptImg = AES256Chiper.Decrypt(EncryptImg, "123456789");
                // Bitmap decryptBitmap = StringToBitmap(DecryptImg);
                //  iv_bitmap.setImageBitmap(decryptBitmap);

                // String 압축
                //String shortEncryptImg = compressString(EncryptImg);
                //Log.e("String압축", shortEncryptImg);

                // String EncryptImg1;
                //EncryptImg1 = EncryptImg.substring(0, 2000);
                // String EncryptImg2 = EncryptImg.substring(2000);

                // Log.e("EncryptImg1", EncryptImg1);
                //Log.e("EncryptImg2", EncryptImg);

                // 암호화된 이미지 업로드
               // DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                //DatabaseReference conditionRef = mRootRef.child("암호화된 이미지6");
                //conditionRef.setValue(EncryptImg);

                //EncryptImg="";



                // 복호화된 이미지 업로드
              //  DatabaseReference mRootRef2 = FirebaseDatabase.getInstance().getReference();
              //  DatabaseReference conditionRef2 = mRootRef2.child("복호화된 이미지5");
               // conditionRef2.setValue(DecryptImg);


                //Bitmap StringToBitmap = StringToBitmap(bitmapToString);

                // iv_bitmap.setImageBitmap(StringToBitmap);

                //Log.e("bitmap변환", bitmapToString);


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