package top.sailliao.bing.test.kx;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AddImage {

    /**
     * Word中写入图片示例
     *
     * @throws Exception
     */
    public static void writeToWord(String out, String imgPath, String in) throws Exception {
        FileOutputStream fos = new FileOutputStream(out);
        XWPFDocument document = new XWPFDocument(Files.newInputStream(Paths.get(in)));
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        XWPFParagraph paragraph = paragraphs.get(paragraphs.size() - 1);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setVerticalAlignment(TextAlignment.TOP);
        XWPFRun run = paragraph.insertNewRun(0);

        int width = Units.toEMU(150);
        int height = Units.toEMU(150);
        run.addPicture(Files.newInputStream(Paths.get(imgPath)), XWPFDocument.PICTURE_TYPE_PNG, "test", width, height);

        document.write(fos);
        System.out.println("=========Word文件生成成功==========");
    }

    public static void main(String[] args) throws Exception {
        writeToWord( "D:\\Git\\Liaozifan\\bing-image\\download\\filereceipt.docx",
                "C:\\Users\\liaoz\\Nutstore\\1\\我的坚果云\\可信\\公章测试\\test1.png",
                "D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\kx\\filereceipt.docx"
        );
    }


}
