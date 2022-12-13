package com.shxex.bwts.common.joinUpdate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinField {

	/**
	 * 被注解的字段对应表字段
	 * @return
	 */
	String column();

	/**
	 * 被注解的字段对应实体字段
	 * @return
	 */
	String field();

}
