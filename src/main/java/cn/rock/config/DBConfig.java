package cn.rock.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.DriverManager;

@Configuration
public class DBConfig {

    @Bean
    public DriverManager driverManager(){
//        DriverManager driverManager = DriverManager.getConnection();
        return null;
    }
    @Bean
    public DataSource dataSource(){
        DruidDataSource mysqlDataSource = new DruidDataSource();
        mysqlDataSource.setUrl("dbc:mysql://localhost:3306/testdb?characterEncoding=utf-8&serverTimezone=GMT%2B8");
        mysqlDataSource.setUsername("root");
        mysqlDataSource.setPassword("rock");
//        MysqlDataSource mysqlDataSource = new MysqlDataSource();
//        mysqlDataSource.setUrl("dbc:mysql://localhost:3306/testdb?characterEncoding=utf-8&serverTimezone=GMT%2B8");
//        mysqlDataSource.setUser("root");
//        mysqlDataSource.setPassword("rock");
        return mysqlDataSource;
    }
}
