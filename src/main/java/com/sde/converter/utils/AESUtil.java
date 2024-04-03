package com.sde.converter.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

// AES stands for Advanced Encryption Standard
// Tutorial -> https://www.baeldung.com/java-aes-encryption-decryption
public class AESUtil {

    private static Logger logger = LoggerFactory.getLogger(AESUtil.class);
    private static final String AES = "AES";
    private static final String UTF_8 = "UTF-8";
    private static final int KEY_LENGTH = 128;
    private static final int ITERATION_COUNT_1 = 1000;
    private static final int ITERATION_COUNT_2 = 1;
    private static final byte[] IV_PARAMETER_SPEC_1 = new byte[]{69, 53, 75, 70, 73, 56, 52, 77, 67, 74, 76, 79, 80, 68, 48, 57};
    private static final byte[] IV_PARAMETER_SPEC_2 = new byte[]{69, 53, 75, 70, 73, 56, 52, 77, 67, 74, 76, 79, 80, 68, 48, 57};
    private static final String SALT = "HG58YZ3CR9";
    private static final String PLAIN_TEXT = "C733C23E93B3EDA6BD83B5AE4C155E14";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static String prefix = "{AES}";

    public static String encryptBase64URLSafeString(String var0, String var1) throws Exception {
        return getIVParameterSpec(var0, var1, IV_PARAMETER_SPEC_2, ITERATION_COUNT_1);
    }

    public static String encryptBase64SafeString(String var0) throws Exception {
        return getIVParameterSpec(var0, PLAIN_TEXT, IV_PARAMETER_SPEC_1, ITERATION_COUNT_2);
    }

    public static String encryptBase64SafeString(String var0, String var1) throws Exception {
        return getIVParameterSpec(var0, var1, IV_PARAMETER_SPEC_1, ITERATION_COUNT_2);
    }

    public static byte[] encryptByte(byte[] var0) throws Exception {
        return encryptByte(var0, PLAIN_TEXT, IV_PARAMETER_SPEC_1, ITERATION_COUNT_2);
    }

    public static byte[] encryptByte(byte[] var0, String var1) throws Exception {
        return encryptByte(var0, var1, IV_PARAMETER_SPEC_1, ITERATION_COUNT_2);
    }

    public static byte[] encryptByte(byte[] var0, String var1, String var2) throws Exception {
        return encryptByte(var0, var1, var2.getBytes(), ITERATION_COUNT_2);
    }

    public static String decryptBase64URLSafeString(String var0, String var1) throws Exception {
        return getPlainText(var0, var1, IV_PARAMETER_SPEC_2, ITERATION_COUNT_1);
    }

    public static String decryptBase64SafeString(String var0) throws Exception {
        return getPlainText(var0, PLAIN_TEXT, IV_PARAMETER_SPEC_1, ITERATION_COUNT_2);
    }

    public static String decryptBase64SafeString(String var0, String var1) throws Exception {
        return getPlainText(var0, var1, IV_PARAMETER_SPEC_1, ITERATION_COUNT_2);
    }

    public static byte[] decrypt(byte[] var0) throws Exception {
        return decrypt(var0, PLAIN_TEXT, IV_PARAMETER_SPEC_1, ITERATION_COUNT_2);
    }

    public static byte[] decrypt(byte[] var0, String var1) throws Exception {
        return encryptByte(var0, var1, IV_PARAMETER_SPEC_1, ITERATION_COUNT_2);
    }

    public static byte[] decrypt(byte[] var0, String var1, String var2) throws Exception {
        return encryptByte(var0, var1, var2.getBytes(), ITERATION_COUNT_2);
    }

    private static String getIVParameterSpec(String var0, String var1, byte[] var2, int var3) throws Exception {
        StringBuilder var4 = new StringBuilder("encrypt = [" + var0 + "], ");

        try {
            byte[] var5 = var0.getBytes(UTF_8);
            var5 = encryptByte(var5, var1, var2, var3);
            String var6 = Base64.encodeBase64URLSafeString(var5);
            var4.append("value = [").append(var6).append("]");
            return var6;
        } catch (Exception var7) {
            throw var7;
        }
    }

    private static String getPlainText(String var0, String var1, byte[] var2, int var3) throws Exception {
        StringBuilder var4 = new StringBuilder("decrypt = [" + var0 + "], ");

        try {
            byte[] var5 = Base64.decodeBase64(var0);
            var5 = decrypt(var5, var1, var2, var3);
            String var6 = new String(var5, UTF_8);
            var4.append("value = [").append(var6).append("]");
            return var6;
        } catch (Exception var7) {
            throw var7;
        }
    }
    public static byte[] encryptByte(byte[] var0, String var1, byte[] var2, int var3) throws Exception {
        SecretKeyFactory var4 = getSecretKeyFactory();
        PBEKeySpec var5 = new PBEKeySpec(var1.toCharArray(), SALT.getBytes(), var3, KEY_LENGTH);
        SecretKey var6 = var4.generateSecret(var5);
        SecretKeySpec var7 = new SecretKeySpec(var6.getEncoded(), AES);
        Cipher var8 = getCipher();
        var8.init(1, var7, new IvParameterSpec(var2));
        return var8.doFinal(var0);
    }

    public static byte[] decrypt(byte[] var0, String var1, byte[] var2, int var3) throws Exception {
        SecretKeyFactory var4 = getSecretKeyFactory();
        PBEKeySpec var5 = new PBEKeySpec(var1.toCharArray(), SALT.getBytes(), var3, KEY_LENGTH);
        SecretKey var6 = var4.generateSecret(var5);
        SecretKeySpec var7 = new SecretKeySpec(var6.getEncoded(), AES);
        Cipher var8 = getCipher();
        var8.init(2, var7, new IvParameterSpec(var2));
        return var8.doFinal(var0);
    }

    private static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance(TRANSFORMATION);
    }

    private static SecretKeyFactory getSecretKeyFactory() throws NoSuchAlgorithmException {
        return SecretKeyFactory.getInstance(ALGORITHM);
    }

    public static void main(String[] var0) throws Exception {
        System.out.println(decryptBase64SafeString("gDEtmYBYJCxpofUf1OAlTQ"));
    }
}
