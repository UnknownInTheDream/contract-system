package cn.newangels.system.util;

import cn.newangels.common.util.SnowflakeIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: TangLiang
 * @date: 2022/1/19 17:25
 * @since: 1.0
 */
@SpringBootTest
public class SnowTest {
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Test
    public void snow() {
        System.out.println(snowflakeIdWorker.nextId());
        System.out.println(snowflakeIdWorker.nextId());
    }
}
