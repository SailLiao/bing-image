package top.sailliao.bing.test;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;

public class CryptoUtil {
    // 测试服
    private static String key = "jyeq2sy1q1alsopz";
    private static String iv = "p2j7sp2msskjh4r7";

    /**
     * aes加密
     */
    public static String aesEncode(String resource) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
        return aes.encryptHex(resource);
    }

    /**
     * aes解密
     */
    public static String aesDecode(String encryptData) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
        return aes.decryptStr(encryptData);
    }

    public static void main(String[] args) {
        System.out.println(aesEncode("123"));
    }

}
