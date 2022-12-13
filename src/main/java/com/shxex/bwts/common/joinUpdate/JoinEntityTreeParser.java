package com.shxex.bwts.common.joinUpdate;

import com.shxex.bwts.common.utils.NameUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinEntityTreeParser {

    public static JoinEntityTree recuseParse(Class<?> aClass, JoinEntityTree parent) {

        JoinEntity annotation = aClass.getAnnotation(JoinEntity.class);
        if (annotation == null) {
            return null;
        }

        JoinEntityTree joinEntityTree = new JoinEntityTree();
        joinEntityTree.setParent(parent);

        String table = annotation.table();
        String joinTable = annotation.joinTable();
        joinEntityTree.setTableName(table);
        joinEntityTree.setJoinTableName(parent != null ? parent.getJoinTableName() : joinTable);


        List<JoinFieldInfo> filedList = new ArrayList<>();
        Map<String, JoinFieldInfo> fieldMap = new HashMap<>();
        joinEntityTree.setFiledList(filedList);
        joinEntityTree.setFieldMap(fieldMap);

        List<JoinEntityTree> childrenList = new ArrayList<>();
        Map<String, JoinEntityTree> childrenMap = new HashMap<>();
        joinEntityTree.setChildrenList(childrenList);
        joinEntityTree.setChildrenMap(childrenMap);

        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            JoinField updateField = field.getAnnotation(JoinField.class);
            JoinFieldInfo joinFieldInfo = new JoinFieldInfo();
            joinFieldInfo.setTableName(joinEntityTree.getTableName());
            joinFieldInfo.setColumnName(updateField != null ? updateField.column() : NameUtil.underscoreName(field.getName()));
            joinFieldInfo.setJdbcType("");//TODO 这个有什么用？

            joinFieldInfo.setFieldName(updateField != null ? updateField.field() : field.getName());
            joinFieldInfo.setJavaType(field.getType().getName());

            joinFieldInfo.setJoinTableName(joinEntityTree.getJoinTableName());
            joinFieldInfo.setJoinColumnName(NameUtil.underscoreName(field.getName()));
            joinFieldInfo.setJoinJdbcType("");//TODO 这个有什么用？

            joinFieldInfo.setJoinFieldName(field.getName());
            joinFieldInfo.setJoinJavaType("");//TODO 这个有什么用？应该是用来辅助生成代码

            filedList.add(joinFieldInfo);
            fieldMap.put(field.getName(), joinFieldInfo);
        }

        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            JoinEntityTree childJoinEntityTree = recuseParse(fieldType, joinEntityTree);
            if (childJoinEntityTree != null) {
                childrenList.add(childJoinEntityTree);
                childrenMap.put(field.getName(), childJoinEntityTree);
            }
        }


        for (Field field : fields) {
            JoinFieldInfo joinFieldInfo = fieldMap.get(field.getName());
            JoinForeignKey joinForeignKey = field.getAnnotation(JoinForeignKey.class);
            if (joinForeignKey == null) {
                continue;
            }
            String foreignKeyChild = joinForeignKey.foreignKeyChild();
            if (StringUtils.isBlank(foreignKeyChild)) {
                //foreignKeyChild 为空，默认关联父亲
                JoinFieldInfo parentField = parent.getFieldMap().get(joinForeignKey.foreignKeyField());
                joinFieldInfo.setForeignKeyTable(parentField.getTableName());
                joinFieldInfo.setForeignKeyColumn(parentField.getColumnName());
                joinFieldInfo.setForeignKeyJoinField(parentField.getJoinFieldName());
                joinFieldInfo.setForeignKeyJoinColumn(parentField.getJoinColumnName());
            } else {
                //foreignKeyChild 不为空，关联指定儿子
                JoinEntityTree childEntity = childrenMap.get(foreignKeyChild);
                JoinFieldInfo childJoinFieldInfo = childEntity.getFieldMap().get(joinForeignKey.foreignKeyField());
                joinFieldInfo.setForeignKeyTable(childEntity.getTableName());
                joinFieldInfo.setForeignKeyColumn(childJoinFieldInfo.getColumnName());
                joinFieldInfo.setForeignKeyJoinField(childJoinFieldInfo.getJoinFieldName());
                joinFieldInfo.setForeignKeyJoinColumn(childJoinFieldInfo.getJoinColumnName());
            }
        }

        return joinEntityTree;

    }
}
