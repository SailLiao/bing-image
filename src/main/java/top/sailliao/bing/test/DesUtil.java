package top.sailliao.bing.test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * 字符串加密工具
 */
@Slf4j
public class DesUtil {

    private static final String PASSWORD_CRYPT_KEY = "EMAIFAN1";
    private final static String DES = "DES";
    private static final String CHECK_CODE = "checkCode";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA256";

    /**
     * 加密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public final static String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes()),
                    PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 加密
     *
     * @param password
     * @return
     * @throws Exception
     */
    public final static String encrypt(String password) {
        try {
            return byte2hex(encrypt(password.getBytes(), PASSWORD_CRYPT_KEY
                    .getBytes()));
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 二行制转字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }


    /**
     * MD5不可逆加密算法
     **/
    public static String MD5Encode(String string) {
        String md5str = "";
        string += "_wemew_salt"; //加盐
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 2 将消息变成byte数组
            byte[] input = string.getBytes();
            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);
            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytes2Hex(buff);
        } catch (Exception e) {
            log.error("异常: {}", e.getMessage(), e);
        }
        return md5str;
    }

    /**
     * MD5不可逆加密算法
     **/
    public static String MD5Encode(String paramStr, String key) {
        String md5str = "";
        paramStr += key; //加盐
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 2 将消息变成byte数组
            byte[] input = paramStr.getBytes();
            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);
            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytes2Hex(buff);
        } catch (Exception e) {
            log.error("异常: {}", e.getMessage(), e);
        }
        return md5str;
    }

    /**
     * 将byte数组转为16进制
     *
     * @param src
     * @return
     */
    public static String bytes2Hex(byte[] src) {
        char[] res = new char[src.length * 2];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >>> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
        }

        return new String(res);
    }

    /**
     * 使用 HMAC-SHA1 签名方法对data进行签名
     *
     * @param data 被签名的字符串
     * @param key  密钥
     * @return 加密后的字符串
     */
    public static String genHMAC(String data, String key) {
        StringBuilder sb = new StringBuilder();
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            for (byte item : rawHmac) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("异常: {}", e.getMessage(), e);
        }
        return sb.toString();
    }

    public static void main(String[] args) {

        // System.out.println(encrypt("8ae33c84-72b4-47e8-95a7-533539b066bf"));

        /*String md5Key = "4uaueys48xn0rpzy";
        Map<String, String> map = new TreeMap<>();
        long now = System.currentTimeMillis();
        map.put("barId", "e5301726-b564-4505-ac6f-485fb9dd2a45");
        map.put("barName", "晚风-97");
        map.put("tableNo", "v10");
        map.put("timeStamp", now + "");
        map.put("userName", "唐大师");
        System.out.println(now);
        String md5 = MD5Encode(ThirdPartyCloudUtil.getUrlParamsByMap(map), md5Key).toLowerCase();
        System.out.println(md5);
        map.put("md5", md5);

        System.out.println(md5Verify(JSONObject.parseObject(JSON.toJSONBytes(map)), md5Key));*/

        // 发送 http 请求
        /*String url = "http://test.wemew.cn/saas/show_press_lamp_text";

        JSONObject req = new JSONObject();
        req.put("barId", "e5301726-b564-4505-ac6f-485fb9dd2a45");
        req.put("barName", "晚风-97");
        req.put("tableNo", "v10");
        req.put("timeStamp", now + "");
        req.put("userName", "唐大师");
        req.put("md5", md5);*/

        /*req.put("barId", "e5301726-b564-4505-ac6f-485fb9dd2a45");
        req.put("barName", "晚风-97");
        req.put("tableNo", "32");
        req.put("timeStamp", now + "");
        req.put("userName", "唐大师");
        req.put("md5", md5);*/

        /*String result = HttpUtil.post(url, req.toJSONString());
        System.out.println(result);*/

        /*Date date1 = cn.hutool.core.date.DateUtil.parse("2026-05-20 10:00:00", DatePattern.NORM_DATETIME_PATTERN).toJdkDate();
        Date date2 = cn.hutool.core.date.DateUtil.parse("2026-05-22 11:00:00", DatePattern.NORM_DATETIME_PATTERN).toJdkDate();

        Date ACTIVITY_520_ARTIST_RANK_TIME = cn.hutool.core.date.DateUtil.parse("2026-05-22 12:00:00", DatePattern.NORM_DATETIME_PATTERN).toJdkDate();
        System.out.println(DateUtil.between(date1, ACTIVITY_520_ARTIST_RANK_TIME, DateUnit.SECOND));
        System.out.println(DateUtil.between(date2, ACTIVITY_520_ARTIST_RANK_TIME, DateUnit.SECOND));*/

        /*int a = 300;
        int count = (int) ((a - 250) / 50) + 1;
        System.out.println(count);*/

        /*List<String> all = new ArrayList<>();
        all.add("1");

        List<String> sub = CollUtil.sub(all, 0, 10);
        System.out.println(JSON.toJSONString(sub));*/

        // System.out.println(encrypt("77833fca-56b2-45f3-80ff-39cd67c6ccff"));

        String aiGenerateImg = "https://oss.wemew.com/wemew/aigc/2026-05-26-13/e9ec4b490eea44ca97ce9aa21e59b4cb.mp4;https://oss.wemew.com/wemew/aigc/2026-05-26-13/72dfcb4804d14292a520a8311b3f25be.png;https://oss.wemew.com/wemew/aigc/2026-05-26-13/0c39dba518be4c738a9f5be6a70ef811.png;https://oss.wemew.com/wemew/aigc/2026-05-26-13/70913bc2208d425dbd81a8e26b0be626.png";
        System.out.println(aiGenerateImg.substring(aiGenerateImg.indexOf(";") + 1).replaceAll(";", ","));

    }

    public static boolean md5Verify(JSONObject json, String md5Key) {
        String timeStamp = json.getString("timeStamp");
        String md5 = json.getString("md5");
        if (StringUtils.isEmpty(timeStamp) || StringUtils.isEmpty(md5)) {
            return false;
        }
        long clientTimeStamp = Long.valueOf(timeStamp);
        long wmTimeStamp = System.currentTimeMillis();
        /*if (wmTimeStamp - clientTimeStamp > 60000) {
            return false;
        }*/
        Map<String, String> map = new TreeMap<>();
        for (String key : json.keySet()) {
            String paraValue = json.getString(key);
            if (!key.equals("md5")) {
                map.put(key, StringUtils.isNotBlank(paraValue) ? paraValue : "");
            }
        }
        String wmToken = DesUtil.MD5Encode(ThirdPartyCloudUtil.getUrlParamsByMap(map), md5Key).toLowerCase();
        if (!md5.equals(wmToken)) {
            return false;
        }
        return true;
    }
}
