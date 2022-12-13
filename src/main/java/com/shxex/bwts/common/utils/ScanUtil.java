package com.shxex.bwts.common.utils;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shxex.bwts.common.HQScanPackage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ljp
 */
@Slf4j
public class ScanUtil {

    public static Map<String, Class<?>> ScanEntity(String... packNames) {
        Map<String, Class<?>> res = new ConcurrentHashMap<>();
        HQScanPackage scanPackage = new HQScanPackage();
        for (String packName : packNames) {
            scanPackage.addPackage(packName);
        }
        scanPackage.setListener(clazz -> {
            TableName annotation = clazz.getAnnotation(TableName.class);
            if (annotation != null) {
                String tableName = annotation.value();
                res.put(tableName, clazz);
            }
        });
        scanPackage.scan();
        return res;
    }

    public static Map<String, Class<?>> ScanService(String... packNames) {
        Map<String, Class<?>> res = new ConcurrentHashMap<>();
        HQScanPackage scanPackage = new HQScanPackage();
        for (String packName : packNames) {
            scanPackage.addPackage(packName);
        }
        scanPackage.setListener(clazz -> {
            Service annotation = clazz.getAnnotation(Service.class);
            if (annotation == null) {
                return;
            }
            Type superclass = clazz.getGenericSuperclass();
            if (superclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) superclass;
                Type actType = parameterizedType.getActualTypeArguments()[1];
                TableName tableName = getTableName(actType);
                if (tableName == null) return;
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    if (IService.class.isAssignableFrom(anInterface)) {
                        res.put(tableName.value(), anInterface);
                    }
                }
            }
        });
        scanPackage.scan();
        return res;
    }

    public static Map<String, Class<?>> ScanRepository(String... packNames) {
        Map<String, Class<?>> res = new ConcurrentHashMap<>();
        HQScanPackage scanPackage = new HQScanPackage();
        for (String packName : packNames) {
            scanPackage.addPackage(packName);
        }
        scanPackage.setListener(clazz -> {
            if (!ElasticsearchRepository.class.isAssignableFrom(clazz)) {
                return;
            }
            Type superclass = clazz.getGenericSuperclass();
            if (!(superclass instanceof ParameterizedType)) {
                return;
            }
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            Type actType = parameterizedType.getActualTypeArguments()[0];
            TableName tableName = getTableName(actType);
            if (tableName == null) return;
            res.put(tableName.value(), clazz);
        });
        scanPackage.scan();
        return res;
    }

    private static TableName getTableName(Type actType) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(actType.getTypeName());
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        TableName tableName = aClass.getDeclaredAnnotation(TableName.class);
        if (tableName == null) {
            return null;
        }
        return tableName;
    }

    public static Map<String, IService> getTableServiceMap(ApplicationContext applicationContext) {
        Map<String, IService> serviceMap = new HashMap<>();
        Map<String, IService> beansOfType = applicationContext.getBeansOfType(IService.class);
        for (String beanName : beansOfType.keySet()) {
            IService service = beansOfType.get(beanName);
            Class entityClass = service.getEntityClass();
            TableName annotation = (TableName) entityClass.getAnnotation(TableName.class);
            if (annotation != null) {
                serviceMap.put(annotation.value(), service);
            } else {
                serviceMap.put(NameUtil.underscoreName(entityClass.getSimpleName()), service);
            }
        }
        return serviceMap;
    }

}
