package top.sailliao.bing.test;



import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HZK12Converter4 {

    // 画布的高度 也就是字体的大小
    private static final int CANVAS_HEIGHT = 12;
    // 汉字的字体宽度
    private static final int FONT_SIZE = 12;
    // 英文 数字 字符的宽度
    private static final int FONT_SIZE_S = 8;

    public static String[][] convertTextToCanvas(String[] text, int width) throws Exception {
        int leftPadding = 0;

        // 创建画布数据存储
        List<String[]> canvas = new ArrayList<>();

        // 处理每一行像素
        for (int row = 0; row < CANVAS_HEIGHT; row++) {
            byte[] rowBytes = new byte[width / 8 + 1];

            // 处理每个字符
            int currentX = leftPadding;

            for (String c : text) {
                /*File file = new File(c);
                if (file.exists()) {

                    // 1. 读取图片文件
                    BufferedImage originalImage = ImageIO.read(file);

                    // 2. 转换为灰度图
                    BufferedImage grayImage = toGrayScale(originalImage);

                    // (可选) 保存处理后的图片
                    ImageIO.write(grayImage, "png", new File("gray.png"));

                    // 3. 缩放到12x12像素
                    BufferedImage scaledImage = scaleImage(grayImage, 12, 12);

                    ImageIO.write(scaledImage, "png", new File("resize.png"));

                    // 4. 二值化处理
                    int[][] matrix = binarizeImage(scaledImage);

                    for (int x = 0; x < 12; x++) {
                        int px = currentX + x;
                        if (px < CANVAS_WIDTH && matrix[row][x] == 0) {
                            int byteIdx = px / 8;
                            rowBytes[byteIdx] |= (1 << (7 - (px % 8)));
                        }
                    }

                    currentX += 12;
                    continue;
                }*/

                // 获取字符在HZK12中的偏移量
                byte[] charData = getCharacterData(c);
                if (charData == null) {
                    System.out.println("未获取到字节数组:" + c);
                    continue;
                }

                boolean isSingleByte = charData.length == 12;
                if (isSingleByte) {
                    // ==== 修复点2：单字节字符特殊处理 ====
                    byte b = charData[row];

                    // 处理8位点阵（英文/数字）只处理8位
                    for (int i = 0; i < FONT_SIZE_S; i++) {
                        int pixelX = currentX + i;
                        if (pixelX < width) {
                            // 像素在画布上的横坐标
                            int byteIdx = pixelX / 8; // 1字节=8像素 除以8得到该像素属于哪个字节
                            // pixelX % 8 获取像素在字节内的位置（0-7）
                            // 字节高位（MSB）对应左侧像素（位7）
                            // 字节低位（LSB）对应右侧像素（位0）
                            // pixelX=10（10%8=2）→ bitPosition=5（7-2）
                            int bitPosition = 7 - (pixelX % 8);

                            // b 是字符点阵的当前行字节（8位表示8个像素）
                            // i 是字符内的像素偏移（0=最左侧，7=最右侧）
                            // (1 << (7 - i)) 创建掩码检查对应位：
                            // 当i=0（最左）：检查位7（10000000）
                            // 当i=7（最右）：检查位0（00000001）
                            if ((b & (1 << (7 - i))) != 0) { // 从左到右处理
                                // 用位操作设置目标字节的指定位为1
                                // 例如：bitPosition=5 → 00100000
                                // |= 操作保留其他位不变，只设置目标位
                                rowBytes[byteIdx] |= (byte) (1 << bitPosition);
                            }
                        }
                    }
                    // 画布的宽度只增加8位
                    currentX += 8;
                } else {
                    // 获取当前行对应的字节
                    int byteIndex = row * 2;
                    if (byteIndex < charData.length) {
                        byte byte1 = charData[byteIndex];
                        byte byte2 = (byteIndex + 1 < charData.length) ? charData[byteIndex + 1] : 0;

                        // 合并两个字节的12位有效数据
                        int bits = ((byte1 & 0xFF) << 4) | ((byte2 & 0xFF) >>> 4);

                        // 将12位数据放入画布
                        for (int i = 0; i < FONT_SIZE; i++) {
                            int pixelX = currentX + i;
                            if (pixelX < width) {
                                int byteIdx = pixelX / 8;
                                int bitPosition = 7 - (pixelX % 8); // MSB first
                                if (((bits >> (FONT_SIZE - 1 - i)) & 1) == 1) {
                                    rowBytes[byteIdx] |= (byte) (1 << bitPosition);
                                }
                            }
                        }
                    }
                    // 汉字 移动到下一个字符位置
                    currentX += 12;
                }
            }

            // 将字节数组转换为十六进制字符串
            String[] hexRow = new String[rowBytes.length];
            for (int i = 0; i < rowBytes.length; i++) {
                hexRow[i] = "0x" + String.format("%02X", rowBytes[i] & 0xFF);
            }
            canvas.add(hexRow);
        }

        return canvas.toArray(new String[0][]);
    }

    public static byte[] getMat(int ascii) {
        int offset_step = FONT_SIZE;

        if (ascii > 127 || ascii < 32) {
            System.out.println("不是单字节字符");
            return null;
        }

        int offset = (ascii - 32) * offset_step;

        byte[] cbuf;

        try {

            cbuf = new byte[offset_step];
            FileInputStream inputStream = new FileInputStream("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\msgothic_asc");
            inputStream.skip(offset);
            if (inputStream.read(cbuf, 0, offset_step) < 0) {
                System.out.println("读取单字节字符失败!");
                return null;
            }
            inputStream.close();
            return cbuf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getCharacterData(String c) throws Exception {

        // 将字符转换为GB2312编码
        byte[] gbBytes = String.valueOf(c).getBytes("GB2312");

        // GB2312汉字应该是双字节
        if (gbBytes.length < 2) {
            // 单字节 是英文和数字和字符
            return getMat(gbBytes[0]);
        }

        byte[] buffer = new byte[24];

        // 获取区码和位码 需要 & 0xFF 转化为无符号整数
        // byte类型是有符号的（范围-128到127）
        // 而GB2312编码的字节值可能大于127
        int area = (gbBytes[0] & 0xFF);
        int pos = (gbBytes[1] & 0xFF);

        int offset = ((area - 0xa1) * 94 + (pos - 0xa1)) * 24;

        FileInputStream inputStream = new FileInputStream("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\fh");
        inputStream.skip(offset);
        if (inputStream.read(buffer, 0, 24) < 0) {
            System.out.println("读取汉字失败!");
            return null;
        }

        inputStream.close();

        return buffer;
    }


    // 转换为灰度图
    private static BufferedImage toGrayScale(BufferedImage original) {
        BufferedImage grayImage = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g2d = grayImage.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();

        return grayImage;
    }

    // 缩放图片
    private static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = scaledImage.createGraphics();

        // 设置高质量缩放
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();

        return scaledImage;
    }

    // 二值化处理 (使用Otsu自动阈值)
    private static int[][] binarizeImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] matrix = new int[height][width];

        // 获取灰度数据
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        // 计算Otsu阈值
        int threshold = calculateOtsuThreshold(pixels);

        // 二值化处理
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue = pixels[y * width + x] & 0xFF; // 转换为0-255
                matrix[y][x] = (grayValue > threshold) ? 1 : 0; // 1:白色, 0:黑色
            }
        }

        return matrix;
    }

    // Otsu自动阈值算法
    private static int calculateOtsuThreshold(byte[] pixels) {
        int[] histogram = new int[256];

        // 构建直方图
        for (byte pixel : pixels) {
            int value = pixel & 0xFF;
            histogram[value]++;
        }

        // 计算总像素数
        int total = pixels.length;

        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        float sumB = 0;
        int wB = 0;
        int wF = 0;
        float varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];               // 背景权重
            if (wB == 0) continue;

            wF = total - wB;                  // 前景权重
            if (wF == 0) break;

            sumB += (float) (i * histogram[i]);

            float mB = sumB / wB;             // 背景均值
            float mF = (sum - sumB) / wF;     // 前景均值

            // 计算类间方差
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // 更新最大方差
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
    }

    public static int getWidth(String text) {
        int allWidth = 0;
        for (String c : text.split("")) {
            /*File file = new File(c);
            if (file.exists()) {
                allWidth += 12;
                continue;
            }*/
            // 将字符转换为GB2312编码
            byte[] gbBytes = new byte[0];
            try {
                gbBytes = c.getBytes("GB2312");
            } catch (UnsupportedEncodingException e) {
                continue;
            }
            // GB2312汉字应该是双字节
            if (gbBytes.length < 2) {
                allWidth += 8;
            } else {
                allWidth += 12;
            }
        }
        return allWidth;
    }

    public static String getAllInLine(String str, int width) {
        log.info("开始转换 {}", str);
        String[] text = str.split("");
        String[][] hexData = new String[0][];
        try {
            hexData = convertTextToCanvas(text, width);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < hexData.length; row++) {
            for (int col = 0; col < hexData[row].length; col++) {
                sb.append(hexData[row][col].substring(2));
                /*if (col < hexData[row].length - 1) {
                    sb.append(", ");
                }*/
            }
        }
        log.info("转换结果: {}", sb);
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String str = "感谢蓝D.567DP豪气爆灯，尽情享受美好的夜晚吧";
        int width = getWidth(str);
        System.out.println(width);
        getAllInLine(str, width);
    }
}
