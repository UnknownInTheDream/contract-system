package cn.newangels.system.aspose;

import com.aspose.words.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import wordUtil.LicenseLoad;
import wordUtil.conver.HtmlConverUtil;
import wordUtil.conver.WordConverUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author: TangLiang
 * @date: 2022/3/22 10:22
 * @since: 1.0
 */
@SpringBootTest
public class AsposeTest {

    @Test
    public void doc2pdf() {
        LicenseLoad.getLicense(); //验证License 若不验证则转化出的pdf文档会有水印产生
        try {
            long old = System.currentTimeMillis();
            File file = new File("f:\\test\\1-1项目需求调研报告.pdf");  //新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document("f:\\test\\1-1项目需求调研报告.doc");//sourcerFile是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            os.close();
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");  //转化用时
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wordToHtml() throws FileNotFoundException {
        LicenseLoad.getLicense();// 验证License 若不验证则转化出的文件会有水印产生
        InputStream in = new FileInputStream("E:\\miniodata\\contract\\template\\15503168365043625\\维修合同.doc");
        ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();
        String htmlText = "";
        try {
            Document doc = new Document(in);
            HtmlSaveOptions opts = new HtmlSaveOptions(SaveFormat.HTML);
            opts.setExportXhtmlTransitional(true);
            opts.setExportImagesAsBase64(true);
            opts.setExportPageSetup(true);
            doc.save(htmlStream, opts);
            htmlText = new String(htmlStream.toByteArray(), StandardCharsets.UTF_8);
            htmlStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(htmlText);
    }

    @Test
    public void htmlToWord() {
        String html = "";
        LicenseLoad.getLicense();
        try {
            Document doc = new Document();
            DocumentBuilder builder = new DocumentBuilder(doc);
            builder.insertHtml(html);
            //生成doc文件
            doc.save("f:\\test\\test.doc", SaveOptions.createSaveOptions(SaveFormat.DOC));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 其他操作方法
     */
    @Test
    public void otherOperate() {
        //加载监听,用于去除水印
        LicenseLoad.getLicense();
        //apose.words的其他操作,详见相关API
        //   .........
    }

    /**
     * 针对以上 1,2,3 转换操作的转换方法,在二次封装
     */
    @Test
    public void fengzhuang() {
        try {
            String wordPath = "E:\\test.doc";
            //word转html
            FileInputStream inputStream = new FileInputStream(new File(wordPath));
            String htmlStr = WordConverUtil.wordToHtml(inputStream);
            inputStream.close();
            //word 转 pdf
            String pdfPath = "E:\\test.pdf";
            WordConverUtil.wordToPdf(wordPath, pdfPath);
            //html转word
            HtmlConverUtil.htmlToWord(htmlStr, wordPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
