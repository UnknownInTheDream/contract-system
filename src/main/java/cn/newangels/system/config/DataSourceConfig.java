package cn.newangels.system.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据库配置
 *
 * @author: TangLiang
 * @date: 2021/6/18 23:18
 * @since: 1.0
 */
@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "systemDataSource")
    @Qualifier("systemDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource systemDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "systemJdbcTemplate")
    public JdbcTemplate systemJdbcTemplate(@Qualifier("systemDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("systemDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
