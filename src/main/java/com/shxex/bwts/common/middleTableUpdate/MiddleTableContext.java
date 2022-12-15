package com.shxex.bwts.common.middleTableUpdate;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class MiddleTableContext {

    private Map<String, Set<String>> sourceMiddleMap = new HashMap<>();

    public Set<String> setByTableName(String tableName) {
        return sourceMiddleMap.get(tableName);
    }

    public void parseJoinEntity(Class<?> aClass) {
        Map<String, String> cur = MiddleTableParser.recuseParse(aClass);
        if(cur == null){
            return;
        }
        for (String sourceTableName : cur.keySet()) {
            sourceMiddleMap.putIfAbsent(sourceTableName, new HashSet<>());
            sourceMiddleMap.get(sourceTableName).add(cur.get(sourceTableName));
        }
    }

}
