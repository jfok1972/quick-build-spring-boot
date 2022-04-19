package com.jhopesoft.framework.utils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

/**
 * 国密SM4分组密码算法工具类（对称加密）
 *
 * @author jfok1972
 * 
 *         用于登录密码的加密
 * 
 */
public class Sm4Util {

    private static final String ALGORITHM_NAME = "SM4";
    private static final String ALGORITHM_ECB_PKCS5PADDING = "SM4/ECB/PKCS5Padding";

    private static final int DEFAULT_KEY_SIZE = 128;

    static {
        // 防止内存中出现多次BouncyCastleProvider的实例
        if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private Sm4Util() {
    }

    /**
     * 生成密钥
     *
     * @return 密钥16位
     * @throws Exception 生成密钥异常
     */
    public static byte[] generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(DEFAULT_KEY_SIZE, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    /**
     * 加密，SM4-ECB-PKCS5Padding
     *
     * @param data 要加密的明文
     * @param key  密钥16字节，使用Sm4Util.generateKey()生成
     * @return 加密后的密文
     * @throws Exception 加密异常
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        return sm4(data, key, ALGORITHM_ECB_PKCS5PADDING, null, Cipher.ENCRYPT_MODE);
    }

    public static String encrypt(String data, String key) throws Exception {
        return Hex.toHexString(sm4(data.getBytes(StandardCharsets.UTF_8),
                org.apache.commons.codec.binary.Hex.decodeHex(key.toCharArray()), ALGORITHM_ECB_PKCS5PADDING, null,
                Cipher.ENCRYPT_MODE));
    }

    /**
     * 解密，SM4-ECB-PKCS5Padding
     *
     * @param data 要解密的密文
     * @param key  密钥16字节，使用Sm4Util.generateKey()生成
     * @return 解密后的明文
     * @throws Exception 解密异常
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        return sm4(data, key, ALGORITHM_ECB_PKCS5PADDING, null, Cipher.DECRYPT_MODE);
    }

    public static String decrypt(String data, String key) throws Exception {
        return new String(sm4(Hex.decode(data), org.apache.commons.codec.binary.Hex.decodeHex(key.toCharArray()),
                ALGORITHM_ECB_PKCS5PADDING, null, Cipher.DECRYPT_MODE), StandardCharsets.UTF_8);
    }

    /**
     * SM4对称加解密
     *
     * @param input   明文（加密模式）或密文（解密模式）
     * @param key     密钥
     * @param sm4mode sm4加密模式
     * @param iv      初始向量(ECB模式下传NULL)
     * @param mode    Cipher.ENCRYPT_MODE - 加密；Cipher.DECRYPT_MODE - 解密
     * @return 密文（加密模式）或明文（解密模式）
     * @throws Exception 加解密异常
     */
    private static byte[] sm4(byte[] input, byte[] key, String sm4mode, byte[] iv, int mode) throws Exception {
        IvParameterSpec spec = null;
        if (null != iv) {
            spec = new IvParameterSpec(iv);
        }
        SecretKeySpec sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        Cipher cipher = Cipher.getInstance(sm4mode, BouncyCastleProvider.PROVIDER_NAME);
        if (null == spec) {
            cipher.init(mode, sm4Key);
        } else {
            cipher.init(mode, sm4Key, spec);
        }
        return cipher.doFinal(input);
    }

    public static void main(String[] args) throws DecoderException, Exception {
        String txt = "admin";
        String key = "631efd472c1714d63af14ff1a2d6c83a";

        String output = encrypt(txt, key);
        System.out.println(output);

        output = decrypt("3b0eed60e718382fff76e6eaa70e4c66", key);
        System.out.println(output);

    }

}