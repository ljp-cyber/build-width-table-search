package com.shxex.bwts.common;

import com.shxex.bwts.common.utils.ScanUtil;
import com.shxex.bwts.dome.entity.User;
import com.shxex.bwts.dome.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author ljp
 */
@Slf4j
public class TableNameBeanContent {

    private static Map<String, Class<?>> tableEntityClassMap;
    private static Map<String, Class<?>> tableServiceClassMap;
    private static Map<String, Class<?>> tableRepositoryClassMap;

    static {
        init();
    }

    public static Class<?> getEntityClass(String tableName) {
        return tableEntityClassMap.get(tableName);
    }

    public static Class<?> getRepositoryClass(String tableName) {
        return tableRepositoryClassMap.get(tableName);
    }

    private static void init() {
        getTableEntityClassMap();
        getTableRepositoryClassMap();
        getTableServiceClassMap();
    }

    private static Map<String, Class<?>> getTableEntityClassMap() {
        if (tableEntityClassMap == null) {
            synchronized (TableNameBeanContent.class) {
                if (tableEntityClassMap == null) {
                    tableEntityClassMap = ScanUtil.ScanEntity(User.class.getPackage().getName());

                }
            }
        }
        return tableEntityClassMap;
    }

    private static Map<String, Class<?>> getTableServiceClassMap() {
        if (tableServiceClassMap == null) {
            synchronized (TableNameBeanContent.class) {
                if (tableServiceClassMap == null) {
                    tableServiceClassMap = ScanUtil.ScanService(UserServiceImpl.class.getPackage().getName());
                }
            }
        }
        return tableServiceClassMap;
    }

    private static Map<String, Class<?>> getTableRepositoryClassMap() {
        if (tableRepositoryClassMap == null) {
            synchronized (TableNameBeanContent.class) {
                if (tableRepositoryClassMap == null) {
                    tableRepositoryClassMap = ScanUtil.ScanRepository();
                }
            }
        }
        return tableRepositoryClassMap;
    }

}
