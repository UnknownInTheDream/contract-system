package cn.newangels.system.contractor;

import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.ContractorService;
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
import java.math.BigDecimal;

/**
 * 承揽方数据初始化
 *
 * @author: TangLiang
 * @date: 2022/2/11 14:24
 * @since: 1.0
 */
@SpringBootTest
public class ContractorTest {
    @Autowired
    private ContractorService contractorService;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Test
    public void init() throws IOException, InvalidFormatException {
        InputStream inputStream = new FileInputStream("d:\\JSON.xlsx");
        Workbook workbook = WorkbookFactory.create(inputStream);
        final Sheet sheet = workbook.getSheetAt(0);
        int row = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < row; i++) {
            String V_NAME = sheet.getRow(i).getCell(3).toString();
            String V_ADDRESS = sheet.getRow(i).getCell(2).toString();
            String V_LEGAL = sheet.getRow(i).getCell(1).toString();
            String V_REPRESENTITIVE = sheet.getRow(i).getCell(7).toString();
            String V_PHONE = sheet.getRow(i).getCell(8).toString();
            String V_BANK = sheet.getRow(i).getCell(9).toString();
            String V_ACCOUNT = sheet.getRow(i).getCell(4).toString();
            String V_NATURE = sheet.getRow(i).getCell(11).toString();
            String I_REGEREDCAPITAL = sheet.getRow(i).getCell(10).toString();
            String V_LICENSE = sheet.getRow(i).getCell(12).toString();
            contractorService.insertContractor(String.valueOf(snowflakeIdWorker.nextId()), V_NAME, V_ADDRESS, V_LEGAL, V_REPRESENTITIVE, V_PHONE, V_BANK, V_ACCOUNT, V_NATURE, new BigDecimal(I_REGEREDCAPITAL), V_LICENSE, "暂未评价", "admin");
        }
    }

}
