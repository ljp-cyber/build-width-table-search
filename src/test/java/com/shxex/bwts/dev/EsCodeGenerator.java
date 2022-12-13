package com.shxex.bwts.dev;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

class EsCodeGenerator {

    public static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "xxxxxx";
    public static final String URL = "jdbc:mysql://localhost:3306/";
    public static final String AUTHOR = "ljp";


    public static void main(String[] args) {

        // 全局配置
        // 自定义文件命名，注意 %s 会自动填充表实体属性
        GlobalConfig gc = getGlobalConfig();
        gc.setMapperName("%sEsRepository");
        gc.setXmlName("%sEsRepository");
        gc.setServiceName("I%sEsService");
        gc.setServiceImplName("%sEsServiceImpl");
        gc.setControllerName("%sEsController");

        // 数据源配置
        DataSourceConfig dsc = getDataSourceConfig("my_website");

        // 包配置
        PackageConfig pc = getPackageConfig();

        // 自定义模板
        TemplateConfig template = getTemplateConfig();

        // 策略配置
        StrategyConfig strategy = getStrategyConfig(new String[]{"search"});

        // 设置整合
        AutoGenerator mpg = new AutoGenerator();
        // 使用Veloctiy模板
        mpg.setTemplateEngine(new VelocityTemplateEngine());
        mpg.setPackageInfo(pc);
        mpg.setStrategy(strategy);
        mpg.setDataSource(dsc);
        mpg.setGlobalConfig(gc);
        mpg.setTemplate(template);
        // 执行生成
        mpg.execute();

    }

    private static TemplateConfig getTemplateConfig() {
        TemplateConfig template = new TemplateConfig();
        template.setController("/es-templates/controller.vm");
        template.setService("/es-templates/service.vm");
        template.setServiceImpl("/es-templates/serviceImpl.vm");
        template.setMapper("/es-templates/repository.vm");
        template.setEntity("/es-templates/entity.vm");
        template.setXml(null);
        return template;
    }

    private static PackageConfig getPackageConfig() {
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.shxex.bwts.dome");
        pc.setMapper("esRepository");
        pc.setXml(null);
        pc.setEntity("esEntity");
        pc.setController("esController");
        pc.setService("esService");
        pc.setServiceImpl("esService.impl");
        return pc;
    }

    private static StrategyConfig getStrategyConfig(String[] tableNames) {
        StrategyConfig strategy = new StrategyConfig();
        // 表名生成策略(下划线转驼峰)
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 全局大写命名
        strategy.setCapitalMode(true);
        strategy.setEntityLombokModel(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setInclude(tableNames);

        return strategy;
    }

    private static DataSourceConfig getDataSourceConfig(String dataSource) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName(DRIVER_NAME);
        dsc.setUsername(USERNAME);
        dsc.setPassword(PASSWORD);
        dsc.setUrl(URL + dataSource + "?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true");
        return dsc;
    }

    private static GlobalConfig getGlobalConfig() {
        GlobalConfig gc = new GlobalConfig();
        gc.setOpen(false);
        // 是否支持AR模式
        gc.setActiveRecord(false);
        // 作者
        gc.setAuthor(AUTHOR);
        // 生成的路径
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        // 是否覆盖同名文件，默认是false
        gc.setFileOverride(true);
        // 主键策略：自增
        gc.setIdType(IdType.ASSIGN_ID);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(false);
        //  生成Sql片段
        gc.setBaseColumnList(false);
        gc.setSwagger2(true);
        gc.setDateType(DateType.ONLY_DATE);
        return gc;
    }

}