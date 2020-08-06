package com.adoublei.pbl;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import android.util.Base64;

public class AES256Chiper {

   // public static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    //public static String secretKey = "비밀키";

    //AES256 암호화
    public static String Encrypt(String text, String key) throws Exception

    {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] keyBytes= new byte[16];

        byte[] b= key.getBytes("UTF-8");

        int len= b.length;

        if (len > keyBytes.length) len = keyBytes.length;

        System.arraycopy(b, 0, keyBytes, 0, len);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

        cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);



        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

//               BASE64Encoder encoder = new BASE64Encoder();

//               return encoder.encode(results);



        return Base64.encodeToString(results, 0);

    }


}