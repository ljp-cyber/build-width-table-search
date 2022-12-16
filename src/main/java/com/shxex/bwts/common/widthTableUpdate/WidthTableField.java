package com.shxex.bwts.common.widthTableUpdate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WidthTableField {

    /**
     * 被注解的字段对应 源表字段名称
     *
     * @return
     */
    String sourceTableColumn() default "";

    /**
     * 被注解的字段对应 宽表字段名称
     *
     * @return
     */
    String widthTableColumn() default "";

    /**
     * 是否 源表主键
     *
     * @return
     */
    boolean sourceTablePrimaryKey() default false;

    /**
     * 是否 宽表主键
     *
     * @return
     */
    boolean widthTablePrimaryKey() default false;

}
