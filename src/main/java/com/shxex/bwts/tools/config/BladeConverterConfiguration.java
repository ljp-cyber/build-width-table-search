package com.shxex.bwts.tools.config;

import com.shxex.bwts.tools.convert.EnumToStringConverter;
import com.shxex.bwts.tools.convert.StringToEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * blade enum 《-》 String 转换配置
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
public class BladeConverterConfiguration implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new EnumToStringConverter());
		registry.addConverter(new StringToEnumConverter());
	}

}
