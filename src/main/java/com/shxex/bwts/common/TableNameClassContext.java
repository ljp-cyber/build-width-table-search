package com.shxex.bwts.common;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shxex.bwts.common.utils.ScanUtil;
import com.shxex.bwts.common.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * @author ljp
 */
@Slf4j
public class TableNameClassContext {

    private Map<String, Class<?>> tableEntityClassMap;
    private Map<String, Class<?>> buildWidthTableEntityClassMap;
    private Map<String, Class<?>> tableServiceClassMap;
    private Map<String, Class<?>> tableRepositoryClassMap;

    private String[] entityPackages;
    private String[] servicePackages;
    private String[] repositoryPackages;
    private String[] buildWidthEntityPackages;

    public TableNameClassContext(String[] buildWidthEntityPackages,String[] entityPackages, String[] servicePackages, String[] repositoryPackages) {
        this.entityPackages = entityPackages;
        this.servicePackages = servicePackages;
        this.repositoryPackages = repositoryPackages;
        this.buildWidthEntityPackages = buildWidthEntityPackages;
        this.init();
    }

    public Set<String> buildWidthTableNameSet() {
        return buildWidthTableEntityClassMap.keySet();
    }

    public Set<String> tableNameSet() {
        return tableEntityClassMap.keySet();
    }

    public Class<?> getBuildWidthTableEntityClass(String tableName) {
        return buildWidthTableEntityClassMap.get(tableName);
    }

    public Class<?> getEntityClass(String tableName) {
        return tableEntityClassMap.get(tableName);
    }

    public Class<?> getServiceClass(String tableName) {
        return tableServiceClassMap.get(tableName);
    }

    public Class<?> getRepositoryClass(String tableName) {
        return tableRepositoryClassMap.get(tableName);
    }

    public IService<?> getService(String tableName) {
        Class<?> serviceClass = getServiceClass(tableName);
        IService<?> service = (IService<?>) SpringUtil.getBean(serviceClass);
        return service;
    }

    private void init() {
        getTableEntityClassMap();
        getBuildWidthTableEntityClassMap();
        getTableRepositoryClassMap();
        getTableServiceClassMap();
    }

    private Map<String, Class<?>> getBuildWidthTableEntityClassMap() {
        if (buildWidthTableEntityClassMap == null) {
            synchronized (TableNameClassContext.class) {
                if (buildWidthTableEntityClassMap == null) {
                    buildWidthTableEntityClassMap = ScanUtil.ScanEntity(buildWidthEntityPackages);
                }
            }
        }
        return buildWidthTableEntityClassMap;
    }

    private Map<String, Class<?>> getTableEntityClassMap() {
        if (tableEntityClassMap == null) {
            synchronized (TableNameClassContext.class) {
                if (tableEntityClassMap == null) {
                    tableEntityClassMap = ScanUtil.ScanEntity(entityPackages);
                }
            }
        }
        return tableEntityClassMap;
    }

    private Map<String, Class<?>> getTableServiceClassMap() {
        if (tableServiceClassMap == null) {
            synchronized (TableNameClassContext.class) {
                if (tableServiceClassMap == null) {
                    tableServiceClassMap = ScanUtil.ScanService(servicePackages);
                }
            }
        }
        return tableServiceClassMap;
    }

    private Map<String, Class<?>> getTableRepositoryClassMap() {
        if (tableRepositoryClassMap == null) {
            synchronized (TableNameClassContext.class) {
                if (tableRepositoryClassMap == null) {
                    tableRepositoryClassMap = ScanUtil.ScanRepository(repositoryPackages);
                }
            }
        }
        return tableRepositoryClassMap;
    }

}
