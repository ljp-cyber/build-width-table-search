package com.shxex.bwts.common.joinUpdate;

import lombok.Getter;

import java.util.*;

@Getter
public class JoinEntityTreeContent {

    private List<JoinEntityTree> rootJoinEntityTreeList = new ArrayList<>();

    private Map<String, List<JoinEntityTree>> allJoinEntityTreeMap = new HashMap<>();

    public List<JoinEntityTree> listByTableName(String tableName){
        return allJoinEntityTreeMap.get(tableName);
    }

    public void parseJoinEntity(Class<?> aClass) {
        JoinEntityTree joinEntityTree = JoinEntityTreeParser.recuseParse(aClass, null);
        rootJoinEntityTreeList.add(joinEntityTree);
        recuseGroupByTableName(Collections.singletonList(joinEntityTree));
    }

    private void recuseGroupByTableName(List<JoinEntityTree> list) {
        if (list == null) {
            return;
        }
        for (JoinEntityTree joinEntityTree : list) {
            allJoinEntityTreeMap.putIfAbsent(joinEntityTree.getTableName(), new ArrayList<>());
            allJoinEntityTreeMap.get(joinEntityTree.getTableName()).add(joinEntityTree);
            recuseGroupByTableName(joinEntityTree.getChildrenList());
        }
    }

}
