package com.shxex.bwts.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring 整合 mybatis
 */
@Configuration // 当前是一个java 配置类
@PropertySource(value = "classpath:datasource-config-ljp.properties")
@EnableTransactionManagement // 开启事务管理
@MapperScan(basePackages = "com.shxex.bwts.dome.mapper") // mapper 扫描
@ComponentScan("com.shxex.bwts.dome.service") // 扫描 Service 注解
public class MybatisPlusConfig {

    // 获取属性配置文件里面的字符串
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;

    // 1.数据源 DruidDataSource
    @Bean("dataSource") // 在IOC 容器中初始化， 可取名，也可不取名，因为这里只有一个数据源
    public DriverManagerDataSource druidDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }

    // 2.MybatisSqlSessionFactoryBean工厂
    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(
            DriverManagerDataSource dataSource,
            PaginationInterceptor paginationInterceptor) { // 使用依赖注入的形式注入过来
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        // 传入数据源
        factory.setDataSource(dataSource);
        // 分页插件
//        factory.setPlugins(new PaginationInterceptor()); // 这里可以直接new ， 也可以重新配置一个 Bean ，然后通过参数依赖注入进来
        factory.setPlugins(paginationInterceptor); // 这里是通过参数依赖注入，IOC容器中的 Bean 对象
        return factory;
    }

    //3. 分页， 这里在 IOC 容器中单独创建的形式
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    //4. 事务管理， 开启事务使用类注解
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DriverManagerDataSource dataSource) { // 数据源依赖注入
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}

