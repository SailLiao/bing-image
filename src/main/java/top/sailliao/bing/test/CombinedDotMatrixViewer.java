package top.sailliao.bing.test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CombinedDotMatrixViewer extends JFrame {

    // 画布的宽度和高度
    private static final int CANVAS_WIDTH = 120;
    private static final int CANVAS_HEIGHT = 12;
    private static final int FONT_SIZE = 12;
    private static final int FONT_SIZE_S = 8;

    private final String[] input;
    private final String[][] hexData;
    private final BufferedImage previewImage;

    public CombinedDotMatrixViewer(String[] input) throws Exception {
        this.input = input;
        this.hexData = convertInputToCanvas(input);
        this.previewImage = createPreviewImage();

        setTitle("预览 - ");
        setSize(1400, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea(15, 90);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setText(formatHexData());
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.WEST);

        add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(previewImage, 50, 50, CANVAS_WIDTH * 4, CANVAS_HEIGHT * 4, null);

                // 绘制网格
                g.setColor(Color.LIGHT_GRAY);
                for (int x = 0; x <= CANVAS_WIDTH; x++) {
                    g.drawLine(50 + x * 4, 50, 50 + x * 4, 50 + CANVAS_HEIGHT * 4);
                }
                for (int y = 0; y <= CANVAS_HEIGHT; y++) {
                    g.drawLine(50, 50 + y * 4, 50 + CANVAS_WIDTH * 4, 50 + y * 4);
                }

                // 标题信息
                g.setColor(Color.BLACK);
                g.setFont(new Font("宋体", Font.BOLD, 16));
                g.drawString("点阵预览 (放大4倍):", 50, 40);
                g.drawString("画布尺寸: " + CANVAS_WIDTH + "×" + CANVAS_HEIGHT, 50, 50 + CANVAS_HEIGHT * 4 + 20);
            }
        }, BorderLayout.CENTER);

        JLabel statusBar = new JLabel("  支持文字和图片混合显示");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);
    }

    private String formatHexData() {
        StringBuilder sb = new StringBuilder();
        sb.append("转换内容: ").append(input).append("\n");
        sb.append("画布尺寸: ").append(CANVAS_WIDTH).append("×").append(CANVAS_HEIGHT).append("\n");
        sb.append("点阵数据: ").append(hexData[0].length).append("字节/行 × ").append(hexData.length).append("行\n\n");

        for (int row = 0; row < hexData.length; row++) {
            for (int col = 0; col < hexData[row].length; col++) {
                sb.append(hexData[row][col]);
                if (col < hexData[row].length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private BufferedImage createPreviewImage() {
        BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        g.setColor(Color.BLACK);
        for (int row = 0; row < hexData.length; row++) {
            for (int col = 0; col < hexData[row].length; col++) {
                byte b = (byte) Integer.parseInt(hexData[row][col].substring(2), 16);
                for (int bit = 0; bit < 8; bit++) {
                    int pixelX = col * 8 + bit;
                    if (pixelX < CANVAS_WIDTH && ((b & (1 << (7 - bit))) != 0)) {
                        g.fillRect(pixelX, row, 1, 1);
                    }
                }
            }
        }

        g.dispose();
        return image;
    }

    public static String[][] convertInputToCanvas(String[] input) throws Exception {
        List<String[]> canvas = new ArrayList<>();
        int currentX = 0;

        for (int row = 0; row < CANVAS_HEIGHT; row++) {
            byte[] rowBytes = new byte[CANVAS_WIDTH / 8];

            for (String s : input) {
                if (new File(String.valueOf(s)).exists()) {
                    BufferedImage img = ImageIO.read(new File(s));
                    int[][] matrix = binarizeImage(scaleImage(toGrayScale(img), 12, 12));
                    for (int y = 0; y < 12; y++) {
                        for (int x = 0; x < 12; x++) {
                            int px = currentX + x;
                            if (px < CANVAS_WIDTH && matrix[y][x] == 0) {
                                int byteIdx = px / 8;
                                int bitPos = 7 - (px % 8);
                                rowBytes[byteIdx] |= (1 << bitPos);
                            }
                        }
                    }
                    currentX += 12;
                    continue;
                }

                char[] chars = s.toCharArray();
                for (char c : chars) {
                    if (c == ' ' || c == '\t') {
                        currentX += FONT_SIZE_S;
                        continue;
                    }

                    // 如果是文件路径
                    byte[] charData = getCharacterData(String.valueOf(c));
                    if (charData == null) continue;

                    boolean isSingleByte = charData.length == 12;
                    if (isSingleByte) {
                        byte b = charData[row];
                        for (int i = 0; i < FONT_SIZE_S; i++) {
                            int pixelX = currentX + i;
                            if (pixelX < CANVAS_WIDTH) {
                                int byteIdx = pixelX / 8;
                                int bitPosition = 7 - (pixelX % 8);
                                if ((b & (1 << (7 - i))) != 0) {
                                    rowBytes[byteIdx] |= (1 << bitPosition);
                                }
                            }
                        }
                        currentX += FONT_SIZE_S;
                    } else {
                        int byteIndex = row * 2;
                        if (byteIndex < charData.length) {
                            byte byte1 = charData[byteIndex];
                            byte byte2 = (byteIndex + 1 < charData.length) ? charData[byteIndex + 1] : 0;
                            int bits = ((byte1 & 0xFF) << 4) | ((byte2 & 0xFF) >>> 4);
                            for (int i = 0; i < FONT_SIZE; i++) {
                                int pixelX = currentX + i;
                                if (pixelX < CANVAS_WIDTH) {
                                    int byteIdx = pixelX / 8;
                                    int bitPosition = 7 - (pixelX % 8);
                                    if (((bits >> (FONT_SIZE - 1 - i)) & 1) == 1) {
                                        rowBytes[byteIdx] |= (1 << bitPosition);
                                    }
                                }
                            }
                        }
                        currentX += FONT_SIZE;
                    }
                }
            }

            String[] hexRow = new String[rowBytes.length];
            for (int i = 0; i < rowBytes.length; i++) {
                hexRow[i] = "0x" + String.format("%02X", rowBytes[i] & 0xFF);
            }
            canvas.add(hexRow);
        }

        return canvas.toArray(new String[0][]);
    }

    // 图像处理方法
    private static BufferedImage toGrayScale(BufferedImage original) {
        BufferedImage grayImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = grayImage.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        return grayImage;
    }

    private static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    private static int[][] binarizeImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] matrix = new int[height][width];
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        int threshold = calculateOtsuThreshold(pixels);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue = pixels[y * width + x] & 0xFF;
                matrix[y][x] = (grayValue > threshold) ? 1 : 0;
            }
        }
        return matrix;
    }

    private static int calculateOtsuThreshold(byte[] pixels) {
        int[] histogram = new int[256];
        for (byte p : pixels) histogram[p & 0xFF]++;

        int total = pixels.length;
        float sum = 0;
        for (int i = 0; i < 256; i++) sum += i * histogram[i];

        float sumB = 0;
        int wB = 0, wF = 0;
        float varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) continue;
            wF = total - wB;
            if (wF == 0) break;

            sumB += i * histogram[i];
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;
            float varBetween = wB * wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }
        return threshold;
    }

    // 字符处理方法
    public static byte[] getMat(int ascii) {
        int offset_step = FONT_SIZE;
        if (ascii > 127 || ascii < 32) return null;

        int offset = (ascii - 32) * offset_step;
        byte[] cbuf = new byte[offset_step];

        try (FileInputStream inputStream = new FileInputStream("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\ASC12_8")) {
            inputStream.skip(offset);
            if (inputStream.read(cbuf, 0, offset_step) < 0) return null;
            return cbuf;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getCharacterData(String c) throws Exception {
        byte[] gbBytes = c.getBytes("GB2312");
        if (gbBytes.length < 2) return getMat(gbBytes[0]);

        int area = (gbBytes[0] & 0xFF);
        int pos = (gbBytes[1] & 0xFF);
        int offset = ((area - 0xa1) * 94 + (pos - 0xa1)) * 24;

        byte[] buffer = new byte[24];
        try (FileInputStream inputStream = new FileInputStream("HZK12")) {
            inputStream.skip(offset);
            if (inputStream.read(buffer, 0, 24) < 0) return null;
            return buffer;
        }
    }

    public static void main(String[] args) throws Exception {
        // 示例：混合输入文字和图片
        String[] input = new String[]{"你好HH", "D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\input.bmp"};
        SwingUtilities.invokeLater(() -> {
            try {
                new CombinedDotMatrixViewer(input).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
