package top.sailliao.bing.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ThirdPartyCloudUtil {

    protected static byte[] TripleDesEncrypt(byte[] content, byte[] key) throws Exception {
        byte[] icv = new byte[8];
        System.arraycopy(key, 0, icv, 0, 8);
        return TripleDesEncrypt(content, key, icv);
    }

    protected static byte[] TripleDesEncrypt(byte[] content, byte[] key, byte[] icv) throws Exception {
        final SecretKey secretKey = new SecretKeySpec(key, "DESede");
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        final IvParameterSpec iv = new IvParameterSpec(icv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(content);
    }

    protected static byte[] TripleDesDecrypt(byte[] content, byte[] key) throws Exception {
        byte[] icv = new byte[8];
        System.arraycopy(key, 0, icv, 0, 8);
        return TripleDesDecrypt(content, key, icv);
    }

    protected static byte[] TripleDesDecrypt(byte[] content, byte[] key, byte[] icv) throws Exception {
        final SecretKey secretKey = new SecretKeySpec(key, "DESede");
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        final IvParameterSpec iv = new IvParameterSpec(icv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return cipher.doFinal(content);
    }

    /**
     * 将map转换成url
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    /**
     * 将map转换成url
     * @param map
     * @return
     */
    public static String getEncodeUrlParamsByMap(Map<String, String> map) throws UnsupportedEncodingException {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(),"UTF-8"));
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    /**
     * 将url参数转换成map
     * @param param aa=11&bb=22&cc=33
     * @return
     */
    public static Map<String, String> getUrlParams(String param) {
        Map<String, String> map = new HashMap<>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

    /**
     * 获取加密串
     *
     * @param content
     * @param desKey 加密key
     * @return
     * @throws Exception
     */
    public static String tripleEncrypt(String content, String desKey) {
        byte[] enc = new byte[0];
        try {
            enc = ThirdPartyCloudUtil.TripleDesEncrypt(content.getBytes("utf-8"), desKey.getBytes("utf-8"));
        } catch (Exception e) {
        }
        byte[] enc64 = Base64.encodeBase64(enc);
        return new String(enc64);
    }

    /**
     * 解密加密串
     * @param encrypt
     * @param desKey
     * @return
     * @throws Exception
     */
    public static String tripleDecrypt(String encrypt, String desKey){
        byte[] dec64 = Base64.decodeBase64(encrypt);
        byte[] dec = new byte[0];
        try {
            dec = ThirdPartyCloudUtil.TripleDesDecrypt(dec64, desKey.getBytes("utf-8"));
        } catch (Exception e) {
        }
        return new String(dec);
    }

}
