package top.sailliao.bing.test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class ImageToDotMatrix {

    public static void main(String[] args) {
        try {
            // 1. 读取图片文件
            BufferedImage originalImage = ImageIO.read(new File("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\input.bmp"));

            // 2. 转换为灰度图
            BufferedImage grayImage = toGrayScale(originalImage);

            // (可选) 保存处理后的图片
            ImageIO.write(grayImage, "png", new File("gray.png"));

            // 3. 缩放到12x12像素
            BufferedImage scaledImage = scaleImage(grayImage, 12, 12);

            ImageIO.write(scaledImage, "png", new File("resize.png"));

            // 4. 二值化处理
            int[][] dotMatrix = binarizeImage(scaledImage);

            // 5. 打印点阵结果
            printDotMatrix(dotMatrix);

            // (可选) 保存处理后的图片
            ImageIO.write(scaledImage, "png", new File("output.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    // 打印点阵
    private static void printDotMatrix(int[][] dotMatrix) {
        System.out.println("12x12 Dot Matrix:");
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 12; x++) {
                System.out.print(dotMatrix[y][x] == 1 ? "□" : "■"); // □表示白色，■表示黑色
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
