package com.shxex.bwts.dev;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;

class EsCodeGeneratorTest {

    /**
     * <p>
     * 生成
     * </p>
     */
    public static void main(String[] args) {

        // 全局配置
        /* 自定义文件命名，注意 %s 会自动填充表实体属性！ */
        GlobalConfig gc = getGlobalConfig();
//        gc.setMapperName("%sMapper");
//        gc.setXmlName("%sMapper");
        gc.setMapperName("%sRepository");
        gc.setXmlName("%sRepository");
        gc.setServiceName("I%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");

        // 数据源配置
//        DataSourceConfig dsc = getDataSourceConfig("bladex");
        DataSourceConfig dsc = getDataSourceConfig("smartsl_cloud");

        // 包配置
        PackageConfig pc = getPackageConfig();
        pc.setParent("com.mta.search.elasticsearch.medi");

        // 自定义模板
        TemplateConfig template = getTemplateConfig();


        // 策略配置
//        StrategyConfig strategy = getStrategyConfig(new String[]{
//                "blade_user",
//                "blade_dept",
//                "blade_dict",
//                "blade_dict_biz",
//                "blade_region"
//        });
//        StrategyConfig strategy = getStrategyConfig(new String[]{
//                "common_assessor",
//                "common_customer",
//                "common_hospital"
//        });
        StrategyConfig strategy = getStrategyConfig(new String[]{
                "medi_case_bill",
                "medi_case_injured",
                "medi_case_loss_occur",
                "medi_case_policy",
                "medi_statement_bill",
                "medi_task_bill",
                "medi_entrust_bill",
                "medi_approve_record"
//                "finance_receipt_bill",
//                "finance_verify_detail",
//                "medi_statement_invoice"//关联发票
        });
//        StrategyConfig strategy = getStrategyConfig(new String[]{
//                "property_case_bill",
//                "property_case_injured",
//                "property_case_loss_occur",
//                "property_case_policy",
//                "property_statement_bill",
//                "property_task_bill"
//        });
//        StrategyConfig strategy = getStrategyConfig(new String[]{
//                "platform_case_bill",
//                "platform_case_injured",
//                "platform_case_loss_occur",
//                "platform_case_policy",
//                "platform_statement_bill",
//                "platform_task_bill"
//        });
//        StrategyConfig strategy = getStrategyConfig(new String[]{
//                "car_case_bill",
//                "car_case_injured",
//                "car_case_loss_occur",
//                "car_case_policy",
//                "car_statement_bill",
//                "car_task_bill"
//        });

        // 设置整合
        AutoGenerator mpg = new AutoGenerator();
        mpg.setTemplateEngine(new VelocityTemplateEngine());  // 使用Veloctiy模板
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

        template.setController("src/test/es-templates/controller.vm");
        template.setService("src/test/es-templates/service.vm");
        template.setServiceImpl("src/test/es-templates/serviceImpl.vm");
        template.setMapper("src/test/es-templates/repository.vm");
//        template.setXml("es-template/xml.vm");
        template.setEntity("src/test/es-templates/entity.vm");
//        template.setEntityKt("es-template/entityDTO.vm");
        return template;
    }

    private static PackageConfig getPackageConfig() {
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.mta.search.elasticsearch.common");
        pc.setMapper("repository");
        pc.setXml("repository.xml");

//        pc.setMapper("mapper");
//        pc.setXml("mapper.xml");

        pc.setEntity("entity");
        pc.setController("controller");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        return pc;
    }

    private static StrategyConfig getStrategyConfig(String[] tableNames) {
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);   // 表名生成策略(下划线转驼峰)
        strategy.setCapitalMode(true);                  // 全局大写命名
        strategy.setEntityLombokModel(true);
        strategy.setEntityTableFieldAnnotationEnable(true);

//        strategy.setTablePrefix(new String[]{"tb_", "sys_"});   // 设定表前缀？（没有测试过）
//         strategy.setExclude(new String[]{"test"});   // 排除生成的表

        strategy.setInclude(tableNames);

        return strategy;
    }

    private static DataSourceConfig getDataSourceConfig(String dataSource) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("dev");
        dsc.setPassword("aXzBIGje");
        dsc.setUrl("jdbc:mysql://10.74.20.95:59301/"
                + dataSource
                + "?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true");
        return dsc;
    }

    private static GlobalConfig getGlobalConfig() {
        GlobalConfig gc = new GlobalConfig();
        gc.setOpen(false);
        gc.setActiveRecord(false);       // 是否支持AR模式
        gc.setAuthor("ljp");            // 作者
        gc.setOutputDir("C://jilian//mta-search//src//main//java");      // 生成的路径
        gc.setFileOverride(true);       // 是否覆盖同名文件，默认是false
        gc.setIdType(IdType.ASSIGN_ID);      // 主键策略：自增
        gc.setEnableCache(false);       // XML 二级缓存
        gc.setBaseResultMap(true);      // XML ResultMap
        gc.setBaseColumnList(true);    //  生成Sql片段
        gc.setSwagger2(true);
        gc.setDateType(DateType.ONLY_DATE);
        return gc;
    }

}