package com.shxex.bwts.utils;

import com.baomidou.mybatisplus.annotation.TableName;
//import com.mta.search.elasticsearch.common.entity.BladeUser;
//import com.mta.search.elasticsearch.common.repository.BladeDeptRepository;
//import com.mta.search.elasticsearch.common.service.IBladeUserService;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mta09
 */
public class DataProcessMaps {

    private static Map<String, Class<?>> tableEntityMap;
    private static Map<String, Class<?>> tableServiceMap;
    private static Map<String, Class<?>> tableRepositoryMap;

    public static Map<String, Class<?>> getTableEntityMap() {
        if (tableEntityMap == null) {
            synchronized (DataProcessMaps.class) {
                if (tableEntityMap == null) {
                    tableEntityMap = new ConcurrentHashMap<>();
                    ScanEntity();
                }
            }
        }
        return tableEntityMap;
    }

    public static Map<String, Class<?>> getTableServiceMap() {

        getTableEntityMap();

        if (tableServiceMap == null) {
            synchronized (DataProcessMaps.class) {
                if (tableServiceMap == null) {
                    tableServiceMap = new ConcurrentHashMap<>();
                    ScanService();
                }
            }
        }
        return tableServiceMap;
    }

    public static Map<String, Class<?>> getTableRepositoryMap() {

        getTableEntityMap();

        if (tableRepositoryMap == null) {
            synchronized (DataProcessMaps.class) {
                if (tableRepositoryMap == null) {
                    tableRepositoryMap = new ConcurrentHashMap<>();
                    ScanRepository();
                }
            }
        }
        return tableRepositoryMap;
    }


    private static void ScanEntity() {
        HQScanPackage scanPackage = new HQScanPackage();
//        scanPackage.addPackage(BladeUser.class.getPackage().getName());
        scanPackage.setFilter(clazz -> true);
        scanPackage.setListener(clazz -> {
            TableName annotation = clazz.getAnnotation(TableName.class);
            if (annotation != null) {
                String tableName = annotation.value();
                tableEntityMap.put(tableName, clazz);
            }
        });
        scanPackage.scan();
    }

    private static void ScanService() {
        HQScanPackage scanPackage = new HQScanPackage();
//        scanPackage.addPackage(IBladeUserService.class.getPackage().getName());
        scanPackage.setFilter(clazz -> true);
        scanPackage.setListener(clazz -> {
            Service annotation = clazz.getAnnotation(Service.class);
            if (annotation != null) {
                String clazzName = clazz.getSimpleName();
                String subTableName = clazzName.replace("ServiceImpl", "");
                String tableName = NameUtil.underscoreName(subTableName);
                if (tableEntityMap.containsKey(tableName)) {
                    tableServiceMap.put(tableName, clazz);
                }
            }
        });
        scanPackage.scan();
    }

    private static void ScanRepository() {
        HQScanPackage scanPackage = new HQScanPackage();
//        scanPackage.addPackage(BladeDeptRepository.class.getPackage().getName());
        scanPackage.setFilter(clazz -> true);
        scanPackage.setListener(clazz -> {
            String clazzName = clazz.getSimpleName();
            String subTableName = clazzName.replace("Repository", "");
            String tableName = NameUtil.underscoreName(subTableName);
            System.out.println("=======" + tableName);
            if (tableEntityMap.containsKey(tableName)) {
                tableRepositoryMap.put(tableName, clazz);
            }
        });
        scanPackage.scan();
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
