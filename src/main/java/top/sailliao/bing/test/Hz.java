package top.sailliao.bing.test;

import java.io.File;
import java.io.RandomAccessFile;

public class Hz {

    public static void main(String[] args) throws Exception {
        Print("你好");
    }

    //获取区位码
    private static String bytes2HexString(byte b) {
        return bytes2HexString(new byte[]{b});
    }

    private static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static int[] getLocation(String str) throws Exception {
        byte[] bs = str.getBytes("GB2312");
        int[] s = new int[str.length() * 2];
        for (int i = 0; i < bs.length; i++) {
            int a = Integer.parseInt(bytes2HexString(bs[i]), 16);
            s[i] = (a - 0x80 - 0x20);
        }
        return s;
    }


    public static void Print(String str) throws Exception {
        File file = new File("HZK12");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        int i, j, k;
        int offset;
        int flag;
        byte[] buffer = new byte[24];

        char[] key = {
                0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
        };
        if (!file.canRead()) {
            System.out.println("err hzk12\n");
            return;
        }


        for (int a = 0; a < str.length() * 2; a += 2) {
            int[] location = getLocation(str);

            offset = (94 * (location[a] - 1) + (location[a + 1] - 1)) * 24;

            raf.seek(offset);
            raf.read(buffer, 0, 24);

            for (k = 0; k < 12; k++) {
                for (j = 0; j < 2; j++) {
                    for (i = 0; i < 8; i++) {
                        flag = buffer[k * 2 + j] & key[i];
                        if (flag > 1)
                            System.out.print("● ");
                        else
                            System.out.print("○ ");
                    }
                }
                System.out.print("\n");
            }
            System.out.print("uchar code key[32] = {");
            for (k = 0; k < 31; k++) {
                System.out.printf("0x%02X,", buffer[k]);
            }
            System.out.printf("0x%02X};\n", buffer[31]);
            System.out.print("\n");
        }
        raf.close();

    }

}
