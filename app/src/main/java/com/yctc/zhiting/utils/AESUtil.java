package com.yctc.zhiting.utils;

import android.util.Base64;

import com.yctc.zhiting.utils.udp.ByteUtil;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 高级对称加密工具类
 */
public class AESUtil {

    public static final String PKCS7 = "AES/CBC/PKCS7Padding";


    /**
     * AES算法加密
     * @param data 要加密的数据
     * @param key  密钥
     * @param mode 加密模式
     * @return 密文
     */
    public static byte[] encryptAES(byte[] data, String key, String mode){
        try {
            System.out.println("key值:"+key);
            byte[] decryptKeyDta =  Md5Util.getMD5(ByteUtil.md5Str2Byte(key));
            byte[] ivData = ByteUtil.byteMergerAll(decryptKeyDta, ByteUtil.md5Str2Byte(key));
            byte[] ivEncryptedData = Md5Util.getMD5(ivData);

            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (decryptKeyDta.length % base != 0) {
                int groups = decryptKeyDta.length / base + (decryptKeyDta.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(decryptKeyDta, 0, temp, 0, decryptKeyDta.length);
                decryptKeyDta = temp;
            }
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec keyspec = new SecretKeySpec(decryptKeyDta, "AES");
            Cipher cipher = Cipher.getInstance(mode, "BC");
            IvParameterSpec ivspec = new IvParameterSpec(ivEncryptedData);

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            byte[] encrypt = cipher.doFinal(data);
            String originalString = ByteUtil.bytesToHex(encrypt);
            return encrypt;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES算法加密
     * @param data 要加密的数据
     * @param mode 加密模式
     * @return 密文
     */
    public static byte[] encryptAES(byte[] data, byte[] decryptKeyDta, byte[] ivEncryptedData, String mode){
        try {

            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (decryptKeyDta.length % base != 0) {
                int groups = decryptKeyDta.length / base + (decryptKeyDta.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(decryptKeyDta, 0, temp, 0, decryptKeyDta.length);
                decryptKeyDta = temp;
            }
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec keyspec = new SecretKeySpec(decryptKeyDta, "AES");
            Cipher cipher = Cipher.getInstance(mode, "BC");
            IvParameterSpec ivspec = new IvParameterSpec(ivEncryptedData);

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            byte[] encrypt = cipher.doFinal(data);
            String originalString = ByteUtil.bytesToHex(encrypt);
            return encrypt;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @Description AES算法解密密文
     * @param data 密文
     * @param key 密钥，长度16
     * @return 明文
     */
    public static String decryptAES(byte[] data, String key, String mode) {
        try {
            String decryptKeyMD5 = Md5Util.getMD5(key);
            byte[] decryptKeyDta = ByteUtil.md5Str2Byte(decryptKeyMD5);
            byte[] ivData = ByteUtil.byteMergerAll(decryptKeyDta, key.getBytes());
            byte[] ivEncryptedData = Md5Util.getMD5(ivData);

            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (decryptKeyDta.length % base != 0) {
                int groups = decryptKeyDta.length / base + (decryptKeyDta.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(decryptKeyDta, 0, temp, 0, decryptKeyDta.length);
                decryptKeyDta = temp;
            }
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec keyspec = new SecretKeySpec(decryptKeyDta, "AES");
            Cipher cipher = Cipher.getInstance(mode, "BC");
            IvParameterSpec ivspec = new IvParameterSpec(ivEncryptedData);

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(data);
            String originalString = ByteUtil.bytesToHex(original);
            return originalString.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Description AES算法解密密文
     * @param data 密文
     * @return 明文
     */
    public static String decryptAES(byte[] data, byte[] decryptKeyDta, byte[] ivEncryptedData, String mode, boolean isHexStr) {
        try {

            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (decryptKeyDta.length % base != 0) {
                int groups = decryptKeyDta.length / base + (decryptKeyDta.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(decryptKeyDta, 0, temp, 0, decryptKeyDta.length);
                decryptKeyDta = temp;
            }
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec keyspec = new SecretKeySpec(decryptKeyDta, "AES");
            Cipher cipher = Cipher.getInstance(mode, "BC");
            IvParameterSpec ivspec = new IvParameterSpec(ivEncryptedData);

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(data);
            String originalString = isHexStr ? ByteUtil.bytesToHex(original) : new String(original, "UTF-8");
            return originalString;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
