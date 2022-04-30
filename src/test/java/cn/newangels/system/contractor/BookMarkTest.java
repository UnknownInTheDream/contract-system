package cn.newangels.system.contractor;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Bookmarks;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author: TangLiang
 * @date: 2022/3/11 16:56
 * @since: 1.0
 */
@SpringBootTest
public class BookMarkTest {

    @Test
    public void getBookMark() {
        try {
            InputStream is = new FileInputStream("E:\\miniodata\\contract\\template\\15503168365043625\\维修合同.doc");
            HWPFDocument doc = new HWPFDocument(is);
            //XWPFDocument
            Bookmarks bookmarks = doc.getBookmarks();
            for (int i = 0; i < bookmarks.getBookmarksCount(); i++) {
                Range range = new Range(bookmarks.getBookmark(i).getStart(), bookmarks.getBookmark(i).getEnd(), doc);
                range.insertAfter("测试" + i);
            }
            FileOutputStream outStream = null;
            outStream = new FileOutputStream("E:\\miniodata\\contract\\template\\15503168365043625\\test.doc");
            doc.write(outStream);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getDocsBookMark() {
        try {
            InputStream is = new FileInputStream("E:\\miniodata\\contract\\template\\15503168365043625\\测试.docx");
            XWPFDocument document = new XWPFDocument(is);
            //BookMarks bookMarks = new BookMarks(document);
            //List<BookMark> list = (List<BookMark>) bookMarks.getBookmarkList();
            //for (int i = 0; i < list.size(); i++) {
            //    BookMark bookMark = list.get(i);
            //    bookMark.insertTextAtBookMark("测试" + i, BookMark.INSERT_AFTER);
            //}

            List<XWPFParagraph> paragraphList = document.getParagraphs();
            for (XWPFParagraph xwpfParagraph : paragraphList) {
                CTP ctp = xwpfParagraph.getCTP();
                for (int dwI = 0; dwI < ctp.sizeOfBookmarkStartArray(); dwI++) {
                    CTBookmark bookmark = ctp.getBookmarkStartArray(dwI);
                    //if (dataMap.containsKey(bookmark.getName())) {
                    XWPFRun run = xwpfParagraph.createRun();
                    //run.setText(dataMap.get(bookmark.getName()));
                    run.setText("测试" + dwI);
                    Node firstNode = bookmark.getDomNode();
                    Node nextNode = firstNode.getNextSibling();
                    //xwpfParagraph.getCTP().getDomNode().insertBefore(run.getCTR().getDomNode(), nextNode);
                    firstNode.appendChild(run.getCTR().getDomNode());
                    //while (nextNode != null) {
                    //    // 循环查找结束符
                    //    String nodeName = nextNode.getNodeName();
                    //    if (nodeName.equals("w:bookmarkEnd")) {
                    //        break;
                    //    }
                    //    // 删除中间的非结束节点，即删除原书签内容
                    //    Node delNode = nextNode;
                    //    nextNode = nextNode.getNextSibling();
                    //
                    //    ctp.getDomNode().removeChild(delNode);
                    //}
                    //if (nextNode == null) {
                    //    // 始终找不到结束标识的，就在书签前面添加
                    //    //ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), firstNode);
                    //    ctp.getDomNode().appendChild(run.getCTR().getDomNode());
                    //} else {
                    //    // 找到结束符，将新内容添加到结束符之前，即内容写入bookmark中间
                    //    ctp.getDomNode().insertBefore(run.getCTR().getDomNode(), nextNode);
                    //}
                    //}
                }
            }

            FileOutputStream outStream = null;
            outStream = new FileOutputStream("E:\\miniodata\\contract\\template\\15503168365043625\\test.docx");
            document.write(outStream);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
