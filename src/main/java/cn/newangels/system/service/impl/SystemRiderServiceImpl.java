package cn.newangels.system.service.impl;

import cn.newangels.common.exception.InsertbatchException;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.SystemRiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件
 *
 * @author: MengLuLu
 * @date: 2022/2/21 10:15
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SystemRiderServiceImpl implements SystemRiderService {
    private final JdbcTemplate systemJdbcTemplate;
    private final SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public int insertSystemRider(String V_TOPIC, String V_DESC, String V_FILETYPE, MultipartFile[] multipartFiles, String V_PERCODE) throws IOException {
        String V_FILENAME;
        int I_FILESIZE;
        byte[] B_FILE;
        String sql = "insert into SYS_RIDER (I_ID, V_TOPIC, V_DESC, V_FILENAME, V_FILETYPE, I_FILESIZE, B_FILE, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?, ?)";
        for (MultipartFile multipartFile : multipartFiles) {
            V_FILENAME = multipartFile.getOriginalFilename();
            I_FILESIZE = Integer.parseInt(String.valueOf(multipartFile.getSize() / 1024));
            B_FILE = multipartFile.getBytes();
            if (systemJdbcTemplate.update(sql, String.valueOf(snowflakeIdWorker.nextId()), V_TOPIC, V_DESC, V_FILENAME, V_FILETYPE, I_FILESIZE, B_FILE, V_PERCODE) == 0) {
                throw new InsertbatchException("上传文件失败");
            }
        }
        return 1;
    }
}