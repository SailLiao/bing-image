package top.sailliao.bing.test;

import cn.hutool.core.io.FileUtil;

import java.util.Base64;

public class Decode {

    public static void main(String[] args) {
        String str = "";

        byte[] imageBytes = Base64.getDecoder().decode(str);
        FileUtil.writeBytes(imageBytes, "D:\\坚果云\\我的坚果云\\装价车\\image\\result.jpg");

    }

}
