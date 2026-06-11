package top.sailliao.bing.test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SpecialCharacters {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "😄";
        byte[] gbBytes = str.getBytes("GB2312");
        System.out.println(Arrays.toString(gbBytes));
    }
}
