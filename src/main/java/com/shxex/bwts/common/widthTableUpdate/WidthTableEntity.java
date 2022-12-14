package com.shxex.bwts.common.widthTableUpdate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WidthTableEntity {

	/**
	 * 该对象对应事实表或维度表 表名称
	 * @return
	 */
	String sourceTable();

	/**
	 * 该对象对应宽表 表名称
	 * 不填默认继承父亲的宽表表名
	 * @return
	 */
	String widthTable() default "";

}
