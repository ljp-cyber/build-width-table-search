package com.shxex.bwts.common.utils;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableInfoUtil {

    public static Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    public static String getColumnFieldNameFromCache(String tableName, String column) {
        Map<String, String> map = cache.get(tableName);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            cache.put(tableName, map);
            TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
            map.put(tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
            for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
                map.put(tableFieldInfo.getColumn(), tableFieldInfo.getProperty());
            }
        }
        return map.get(column);
    }

}
