package com.shxex.bwts.common.widthTableUpdate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WidthTableField {

    /**
     * 被注解的字段对应 表字段名称
     *
     * @return
     */
    String sourceTableColumn() default "";

    /**
     * 被注解的字段对应 实体字段名称
     *
     * @return
     */
    String sourceEntityField() default "";

}
