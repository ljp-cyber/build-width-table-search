package com.shxex.bwts.common.middleTableUpdate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MiddleTableField {

    /**
     * 源表 字段名
     *
     * @return
     */
    String sourceColumn();

    /**
     * 中间表 字段名
     *
     * @return
     */
    String middleColumn() default "";

}
