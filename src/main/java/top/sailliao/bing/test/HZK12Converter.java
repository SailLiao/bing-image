package top.sailliao.bing.test;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HZK12Converter extends JFrame {

    // 画布的宽度 也就是屏幕的宽度
    private static final int CANVAS_WIDTH = 120;
    // 画布的高度 也就是字体的大小
    private static final int CANVAS_HEIGHT = 12;
    // 汉字的字体宽度
    private static final int FONT_SIZE = 12;
    // 英文 数字 字符的宽度
    private static final int FONT_SIZE_S = 8;

    private final String text;
    private final String[][] hexData;
    private final BufferedImage previewImage;

    public HZK12Converter(String text) throws Exception {
        this.text = text;
        this.hexData = convertTextToCanvas(text);
        this.previewImage = createPreviewImage();

        setTitle("HZK12 汉字点阵转换 - " + text);
        setSize(1400, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea(15, 90);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
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

                // 绘制标题
                g.setColor(Color.BLACK);
                g.setFont(new java.awt.Font("宋体", java.awt.Font.BOLD, 16));
                g.drawString("点阵预览 (放大4倍):", 50, 40);
                g.drawString("画布尺寸: " + CANVAS_WIDTH + "×" + CANVAS_HEIGHT, 50, 50 + CANVAS_HEIGHT * 4 + 20);
            }
        }, BorderLayout.CENTER);

        JLabel statusBar = new JLabel("  HZK12 点阵转换结果 - 每个汉字占12×12点阵，画布尺寸120×12");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);
    }

    private String formatHexData() {
        StringBuilder sb = new StringBuilder();
        sb.append("转换结果: ").append(text).append("\n");
        sb.append("画布尺寸: ").append(CANVAS_WIDTH).append("×").append(CANVAS_HEIGHT).append("\n");
        sb.append("点阵数据: ").append(hexData[0].length).append("字节/行 × ").append(hexData.length).append("行\n\n");

        for (int row = 0; row < hexData.length; row++) {
            // sb.append(String.format("行%02d: ", row));
            for (int col = 0; col < hexData[row].length; col++) {
                sb.append(hexData[row][col]);
                System.out.print(hexData[row][col].substring(2));
                if (col < hexData[row].length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
            System.out.println();
        }
        System.out.println();
        System.out.println(sb);
        return sb.toString();
    }

    private BufferedImage createPreviewImage() {
        BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 白色背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // 绘制点阵
        g.setColor(Color.BLACK);
        for (int row = 0; row < hexData.length; row++) {
            for (int col = 0; col < hexData[row].length; col++) {
                byte b = (byte) Integer.parseInt(hexData[row][col].substring(2), 16);
                for (int bit = 0; bit < 8; bit++) {
                    int pixelX = col * 8 + bit;
                    if (pixelX < CANVAS_WIDTH) {
                        if ((b & (1 << (7 - bit))) != 0) {
                            g.fillRect(pixelX, row, 1, 1);
                        }
                    }
                }
            }
        }

        g.dispose();
        return image;
    }

    public static String[][] convertTextToCanvas(String text) throws Exception {
        // 计算文字区域尺寸
        // int textWidth = text.length() * FONT_SIZE;
        // 居中
        // int leftPadding = (CANVAS_WIDTH - textWidth) / 2;
        // 靠左
        int leftPadding = 0;

        // 创建画布数据存储
        List<String[]> canvas = new ArrayList<>();

        // 处理每一行像素
        for (int row = 0; row < CANVAS_HEIGHT; row++) {
            byte[] rowBytes = new byte[CANVAS_WIDTH / 8];

            // 处理每个字符
            int currentX = leftPadding;
            for (String c : text.split("")) {
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
                        if (pixelX < CANVAS_WIDTH) {
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
                                rowBytes[byteIdx] |= (1 << bitPosition);
                            }
                        }
                    }
                    // 画布的宽度只增加8位
                    currentX += FONT_SIZE_S;
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
                            if (pixelX < CANVAS_WIDTH) {
                                int byteIdx = pixelX / 8;
                                int bitPosition = 7 - (pixelX % 8); // MSB first
                                if (((bits >> (FONT_SIZE - 1 - i)) & 1) == 1) {
                                    rowBytes[byteIdx] |= (1 << bitPosition);
                                }
                            }
                        }
                    }
                    // 汉字 移动到下一个字符位置
                    currentX += FONT_SIZE;
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
            FileInputStream inputStream = new FileInputStream("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\ASC12_8");
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

        FileInputStream inputStream = new FileInputStream("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\HZK12");
        inputStream.skip(offset);
        if (inputStream.read(buffer, 0, 24) < 0) {
            System.out.println("读取汉字失败!");
            return null;
        }

        inputStream.close();

        return buffer;
    }

    public static void main(String[] args) throws Exception {
//        String text = "世界和平";
//        String text = "好8好";
//        String text = "好A好";
//        String text = "123@qq.com";
//        String text = "wemew爆灯";
//        String text = "😭";
        String text = "你";
        HZK12Converter frame = new HZK12Converter(text);
        frame.setVisible(true);
    }
}
