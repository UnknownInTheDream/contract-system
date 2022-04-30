package cn.newangels.system.dictionary;

import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.DictionaryService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: TangLiang
 * @date: 2022/2/12 18:38
 * @since: 1.0
 */
@SpringBootTest
public class DictionaryTest {
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Test
    public void init() throws IOException, InvalidFormatException {
        InputStream inputStream = new FileInputStream("d:\\JSON.xlsx");
        Workbook workbook = WorkbookFactory.create(inputStream);
        final Sheet sheet = workbook.getSheetAt(0);
        int row = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < row; i++) {
            String V_CODE = sheet.getRow(i).getCell(0).toString();
            String V_NAME = sheet.getRow(i).getCell(1).toString();
            int I_ORDER = i + 1;
            String V_DICTIONARYTYPE = "合同专业";
            String V_OTHER = "";
            dictionaryService.insertDictionary(String.valueOf(snowflakeIdWorker.nextId()), V_CODE, V_NAME, I_ORDER, V_DICTIONARYTYPE, V_OTHER, "admin");
        }
    }
}
