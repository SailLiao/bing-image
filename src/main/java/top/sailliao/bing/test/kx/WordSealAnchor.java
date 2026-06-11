package top.sailliao.bing.test.kx;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.*;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;

import java.io.FileInputStream;
import java.io.FileOutputStream;


public class WordSealAnchor {

    public static void main(String[] args) throws Exception {

        XWPFDocument doc = new XWPFDocument(new FileInputStream("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\kx\\filereceipt.docx"));

        // 创建一个段落用于插入图片（Anchor）
        XWPFParagraph imagePara = doc.createParagraph();
        XWPFRun imageRun = imagePara.createRun();

        FileInputStream is = new FileInputStream("D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\kx\\test1.png");

        // 添加图片（先用inline）
        imageRun.addPicture(
                is,
                Document.PICTURE_TYPE_PNG,
                "seal.png",
                Units.toEMU(140), // 宽
                Units.toEMU(140)  // 高
        );
        is.close();

        // 获取刚刚插入的 drawing
        CTDrawing drawing = imageRun.getCTR().getDrawingArray(0);

        // 转成 Anchor（关键步骤）
        CTInline inline = drawing.getInlineArray(0);
        CTAnchor anchor = convertInlineToAnchor(inline);

        // 设置位置（右下角）
        anchor.setSimplePos2(false);
        anchor.setRelativeHeight(0);
        anchor.setBehindDoc(false); // false=覆盖在文字上
        anchor.setLocked(false);
        anchor.setLayoutInCell(true);
        anchor.setAllowOverlap(true);

        // 水平位置（右侧）
        CTPosH posH = anchor.addNewPositionH();
        posH.setRelativeFrom(STRelFromH.PAGE);
        // posH.addNewPosOffset().setStringValue(String.valueOf(Units.toEMU(400))); // 左右偏移
        posH.setPosOffset(Units.toEMU(350));

        // 垂直位置（底部）
        CTPosV posV = anchor.addNewPositionV();
        posV.setRelativeFrom(STRelFromV.PAGE);
        // posV.addNewPosOffset().setStringValue(String.valueOf(Units.toEMU(500))); // 上下偏移
        posV.setPosOffset(Units.toEMU(535));

        // 替换 inline
        drawing.setAnchorArray(new CTAnchor[]{anchor});
        drawing.removeInline(0);

        // 输出文件
        FileOutputStream out = new FileOutputStream("D:\\Git\\Liaozifan\\bing-image\\download\\1.docx");
        doc.write(out);
        out.close();
        doc.close();
    }

    /**
     * Inline 转 Anchor
     */
    private static CTAnchor convertInlineToAnchor(CTInline inline) {
        CTAnchor anchor = CTAnchor.Factory.newInstance();

        anchor.setGraphic(inline.getGraphic());
        anchor.setDistT(0);
        anchor.setDistB(0);
        anchor.setDistL(0);
        anchor.setDistR(0);

        anchor.setSimplePos2(false);
        CTPoint2D simplePos = anchor.addNewSimplePos();
        simplePos.setX(0);
        simplePos.setY(0);

        // 尺寸
        anchor.setExtent(inline.getExtent());

        // wrap
        anchor.addNewWrapNone();

        // docPr
        anchor.setDocPr(inline.getDocPr());

        // cNvGraphicFramePr
        anchor.setCNvGraphicFramePr(inline.getCNvGraphicFramePr());

        anchor.addNewEffectExtent().setL(0);
        anchor.getEffectExtent().setT(0);
        anchor.getEffectExtent().setR(0);
        anchor.getEffectExtent().setB(0);

        // 获取 graphic
        CTGraphicalObject graphic = anchor.getGraphic();

        // 获取 pic
        CTGraphicalObjectData graphicData = graphic.getGraphicData();
        XmlObject[] xmlObjects = graphicData.selectChildren(new javax.xml.namespace.QName(
                "http://schemas.openxmlformats.org/drawingml/2006/picture",
                "pic"
        ));

        if (xmlObjects.length > 0) {
            CTPicture pic = (CTPicture) xmlObjects[0];

            // 获取 blip
            CTBlip blip = pic.getBlipFill().getBlip();

            // 设置透明度
            CTAlphaModulateFixedEffect alpha = blip.addNewAlphaModFix();
            alpha.setAmt(30000); // 30% 可见（推荐）
        }

        return anchor;
    }

}
