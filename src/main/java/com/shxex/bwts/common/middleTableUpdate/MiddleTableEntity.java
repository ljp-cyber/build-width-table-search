package com.shxex.bwts.common.middleTableUpdate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MiddleTableEntity {

    /**
     * 该对象对应事实表或维度表 表名称
     *
     * @return
     */
    String sourceTable();

    /**
     * 该对象对中间表 表名称
     * 不填则取 @TableName 注解的表名称
     *
     * @return
     */
    String middleTable() default "";

    /**
     * 根据源表哪个字段聚合
     *
     * @return
     */
    String groupColumn();

}
