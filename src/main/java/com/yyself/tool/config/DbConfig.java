package com.yyself.tool.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * @Author yangyu
 * @create 2020/5/20 上午11:32
 */

@Configurable
public class DbConfig {

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
