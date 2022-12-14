package com.shxex.bwts.common.widthTableUpdate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WidthTableForeignKey {

	/**
	 * 被注解的字段关联了父亲的某个字段或者儿子的某个字段
	 * @return
	 */
	String foreignKeyField();

	/**
	 * 默认为空字符串，表示关联父亲；
	 * 若不为空字符串，则填写儿子对象的字段名称，表示 foreignKeyField 关联儿子对象的字段
	 * @return
	 */
	String foreignKeyChild() default "";

}
