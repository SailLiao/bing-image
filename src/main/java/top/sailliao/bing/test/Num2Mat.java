package top.sailliao.bing.test;

import java.io.FileInputStream;
import java.io.IOException;

public class Num2Mat {

    public static void main(String[] args) {
        Num2Mat nm = new Num2Mat(12, 8, '7');
        nm.getMat();
        nm.print();
    }

    private int font_width = 8;
    private int font_height = 12;
    private int size_step = 8;
    private char char_num = '1';
    private byte[] cbuf;
    private char[] key = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};

    Num2Mat(int font_height, int font_width, char char_num) {
        this.font_height = font_height;
        this.font_width = font_width;
        this.char_num = char_num;
    }

    public void getMat() {
        int sizeof_byte = size_step;
        int offset_step = font_width * font_height / sizeof_byte;

        int ascii = (int) char_num;
        if (ascii > 127 || ascii < 32) {
            System.out.println("input char is invaild!");
            return;
        }
        int offset = (ascii - 32) * offset_step;

        try {

            cbuf = new byte[offset_step];
            FileInputStream inputStream = new FileInputStream("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\ASC12_8");
            inputStream.skip(offset);
            if (inputStream.read(cbuf, 0, offset_step) < 0) {
                System.out.println("read failed!");
                return;
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print() {
        if (font_height == 12 || font_height == 48) {
            // 横向取字
            for (int i = 0; i < font_height; i++) {
                for (int j = 0; j < font_width; j++) {
                    int index = i * font_width + j;
                    int flag = cbuf[index / size_step] & key[index % size_step];
                    System.out.print(flag > 0 ? "● " : "○ ");
                }
                System.out.println();
            }
        } else {
            // 纵向取字
            for (int i = 0; i < font_height; i++) {
                for (int j = 0; j < font_width; j++) {
                    int index = j * font_height + i;
                    int flag = cbuf[index / size_step] & key[index % size_step];
                    System.out.print(flag > 0 ? "●" : "○");
                }
                System.out.println();
            }
        }
    }

}
