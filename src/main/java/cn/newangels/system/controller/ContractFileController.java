package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.ContractFileService;
import com.aspose.words.*;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wordUtil.LicenseLoad;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 合同文件
 * //todo 无用接口待删除
 *
 * @author: TangLiang
 * @date: 2022/03/03 10:50
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class ContractFileController {
    private final ContractFileService contractFileService;

    /**
     * 加载合同文件json信息
     */
    @GetMapping("loadContractFileJson")
    @Log(title = "合同文件管理", operateType = "加载合同文件json信息")
    public Map<String, Object> loadContractFileJson(String V_GUID) {
        return BaseUtils.loadSuccess(contractFileService.loadContractFileJson(V_GUID));
    }

    /**
     * 查询合同文件
     */
    @GetMapping("selectContractFile")
    @Log(title = "合同文件管理", operateType = "查询合同文件")
    public Map<String, Object> selectContractFile(String V_GUID, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = contractFileService.selectContractFile(V_GUID, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = contractFileService.countContractFile(V_GUID);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 修改合同文件
     */
    @PostMapping("updateContractFile")
    @Log(title = "合同文件管理", operateType = "修改合同文件")
    public Map<String, Object> updateContractFile(String V_GUID, MultipartFile multipartFile, String V_PERCODE) throws IOException {
        if (contractFileService.updateContractFile(V_GUID, multipartFile, V_PERCODE) == 0) {
            return BaseUtils.failed("修改合同文件失败");
        }
        return BaseUtils.success();
    }

    /**
     * 下载合同文件
     */
    @GetMapping("downloadContractFile")
    @Log(title = "合同文件管理", operateType = "下载合同文件")
    public void downloadContractFile(String V_GUID, String V_FILEPATH, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        contractFileService.downloadContractFile(V_GUID, V_FILEPATH, V_FILENAME, V_PERCODE, request, response);
    }

    /**
     * 加载合同文件pdf
     */
    @GetMapping("loadContractFilePdf")
    @Log(title = "合同文件管理", operateType = "加载合同文件pdf")
    public void loadContractFilePdf(String V_GUID, Boolean hasWaterMark, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> result = contractFileService.loadContractFileBlob(V_GUID);
        byte[] object = (byte[]) result.get("B_FILE");
        InputStream inputStream = new ByteArrayInputStream(object);
        LicenseLoad.getLicense();
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + BaseUtils.getFormatString(request, "合同.pdf"));// 下载模式
        @Cleanup ServletOutputStream out = response.getOutputStream();
        Document doc = new Document(inputStream);
        if (hasWaterMark != null && hasWaterMark) {
            //添加水印
            Resource resource = new ClassPathResource("static/waterMark.jpg");
            DocumentBuilder builder = new DocumentBuilder(doc);
            Shape shape = new Shape(doc, ShapeType.IMAGE);
            shape.getImageData().setImage(resource.getInputStream());
            shape.setWidth(300);
            shape.setHeight(300);
            shape.setHorizontalAlignment(HorizontalAlignment.CENTER);
            builder.moveToHeaderFooter(HeaderFooterType.HEADER_PRIMARY);
            builder.insertNode(shape);
            //编码
            builder.getCellFormat().getBorders().getTop().setLineStyle(LineStyle.NONE);
            builder.getCellFormat().getBorders().getLeft().setLineStyle(LineStyle.NONE);
            builder.getCellFormat().getBorders().getRight().setLineStyle(LineStyle.NONE);
            builder.insertCell();
            builder.write(BaseUtils.getMd5(V_GUID));
        }
        doc.save(out, SaveFormat.PDF);
    }

}
