package com.shxex.bwts.common.widthTableUpdate;

import lombok.Getter;

import java.util.*;

@Getter
public class WidthTableEntityTreeContent {

    private List<WidthTableEntityTree> rootWidthTableEntityTreeList = new ArrayList<>();

    private Map<String, List<WidthTableEntityTree>> allWidthTableEntityTreeMap = new HashMap<>();

    public List<WidthTableEntityTree> listByTableName(String tableName) {
        return allWidthTableEntityTreeMap.get(tableName);
    }

    public void parseJoinEntity(Class<?> aClass) {
        WidthTableEntityTree widthTableEntityTree = WidthTableEntityTreeParser.recuseParse(aClass, null);
        if (widthTableEntityTree == null) {
            return;
        }
        rootWidthTableEntityTreeList.add(widthTableEntityTree);
        recuseGroupByTableName(Collections.singletonList(widthTableEntityTree));
    }

    private void recuseGroupByTableName(List<WidthTableEntityTree> list) {
        if (list == null) {
            return;
        }
        for (WidthTableEntityTree widthTableEntityTree : list) {
            allWidthTableEntityTreeMap.putIfAbsent(widthTableEntityTree.getSourceTableName(), new ArrayList<>());
            allWidthTableEntityTreeMap.get(widthTableEntityTree.getSourceTableName()).add(widthTableEntityTree);
            recuseGroupByTableName(widthTableEntityTree.getChildrenList());
        }
    }

}
