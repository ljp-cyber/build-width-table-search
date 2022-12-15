package com.shxex.bwts.common.middleTableUpdate;

import java.util.HashMap;
import java.util.Map;

public class MiddleTableParser {

    public static Map<String, String> recuseParse(Class<?> entityClass) {

        MiddleTableEntity middleTableEntity = entityClass.getAnnotation(MiddleTableEntity.class);
        if (middleTableEntity == null) {
            return null;
        }

        Map<String, String> res = new HashMap<>();
        String sourceTable = middleTableEntity.sourceTable();
        String middleTable = middleTableEntity.middleTable();

        res.put(sourceTable, middleTable);

        return res;

    }
}
