package com.shxex.bwts.common.widthTableUpdate;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.shxex.bwts.common.utils.NameUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidthTableParser {

    public static WidthTableEntityTree recuseParse(Class<?> widthTableEntityClass, WidthTableEntityTree parent) {

        TableInfoHelper.getTableInfo("");
        WidthTableEntity annotation = widthTableEntityClass.getAnnotation(WidthTableEntity.class);
        if (annotation == null) {
            return null;
        }

        WidthTableEntityTree widthTableEntityTree = new WidthTableEntityTree();
        widthTableEntityTree.setParent(parent);

        String table = annotation.sourceTable();
        String widthTable = annotation.widthTable();
        widthTableEntityTree.setSourceTableName(table);
        widthTableEntityTree.setWidthTableName(parent != null ? parent.getWidthTableName() : widthTable);


        List<WidthTableFieldInfo> filedList = new ArrayList<>();
        Map<String, WidthTableFieldInfo> fieldMap = new HashMap<>();
        widthTableEntityTree.setWidthTableFiledList(filedList);
        widthTableEntityTree.setWidthTableFieldMap(fieldMap);

        List<WidthTableEntityTree> childrenList = new ArrayList<>();
        Map<String, WidthTableEntityTree> childrenMap = new HashMap<>();
        widthTableEntityTree.setChildrenList(childrenList);
        widthTableEntityTree.setChildrenMap(childrenMap);
        widthTableEntityTree.setSourceTablePrimaryKey("id");
        widthTableEntityTree.setWidthTablePrimaryKey("id");
        if (parent != null) {
            widthTableEntityTree.setWidthTablePrimaryKey(parent.getWidthTablePrimaryKey());
        }

        Field[] fields = widthTableEntityClass.getDeclaredFields();
        for (Field field : fields) {
            WidthTableField widthTableField = field.getAnnotation(WidthTableField.class);
            if (widthTableField == null) {
                continue;
            }
            WidthTableFieldInfo widthTableFieldInfo = new WidthTableFieldInfo();
            widthTableFieldInfo.setSourceTableName(widthTableEntityTree.getSourceTableName());
            widthTableFieldInfo.setSourceTableColumnName(StringUtils.isNotBlank(widthTableField.sourceTableColumn()) ? widthTableField.sourceTableColumn() : NameUtil.underscoreName(field.getName()));

            widthTableFieldInfo.setWidthTableName(widthTableEntityTree.getWidthTableName());
            widthTableFieldInfo.setWidthTableColumnName(StringUtils.isNotBlank(widthTableField.widthTableColumn()) ? widthTableField.widthTableColumn() : NameUtil.underscoreName(field.getName()));

            if (widthTableField.widthTablePrimaryKey()) {
                widthTableEntityTree.setWidthTablePrimaryKey(widthTableFieldInfo.getWidthTableColumnName());
            }
            if (widthTableField.sourceTablePrimaryKey()) {
                widthTableEntityTree.setSourceTablePrimaryKey(widthTableFieldInfo.getSourceTableColumnName());
                widthTableEntityTree.setWidthTableColumnForSourcePrimaryKey(widthTableFieldInfo.getWidthTableColumnName());
            }
            filedList.add(widthTableFieldInfo);
            fieldMap.put(field.getName(), widthTableFieldInfo);
        }

        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            WidthTableChild widthTableChild = field.getAnnotation(WidthTableChild.class);
            if (widthTableChild == null) {
                continue;
            }
            WidthTableEntityTree childWidthTableEntityTree = recuseParse(fieldType, widthTableEntityTree);
            if (childWidthTableEntityTree != null) {
                childrenList.add(childWidthTableEntityTree);
                childrenMap.put(childWidthTableEntityTree.getSourceTableName(), childWidthTableEntityTree);
            }
        }


        for (Field field : fields) {
            WidthTableFieldInfo widthTableFieldInfo = fieldMap.get(field.getName());
            if (widthTableFieldInfo == null) {
                continue;
            }
            WidthTableForeignKey widthTableForeignKey = field.getAnnotation(WidthTableForeignKey.class);
            if (widthTableForeignKey == null) {
                widthTableFieldInfo.setForeignKeyRel(WidthTableFieldInfo.FOREIGN_KEY_REL_NONE);
                continue;
            }
            if (StringUtils.isBlank(widthTableForeignKey.foreignKeySourceTable())) {
                //foreignKeySourceTable 为空，默认关联父亲
                WidthTableFieldInfo parentField = parent.getWidthTableFieldMap().get(widthTableForeignKey.foreignKey());
                widthTableFieldInfo.setForeignKeyRel(WidthTableFieldInfo.FOREIGN_KEY_REL_PARENT);
                widthTableFieldInfo.setForeignKeySourceTable(parentField.getSourceTableName());
                widthTableFieldInfo.setForeignKeySourceColumn(parentField.getSourceTableColumnName());
                widthTableFieldInfo.setForeignKeyWidthTableColumn(parentField.getWidthTableColumnName());
            } else {
                //foreignKeySourceTable 不为空，关联指定儿子
                WidthTableEntityTree childEntity = childrenMap.get(widthTableForeignKey.foreignKeySourceTable());
                widthTableFieldInfo.setForeignKeyRel(WidthTableFieldInfo.FOREIGN_KEY_REL_CHILD);
                WidthTableFieldInfo childWidthTableFieldInfo = childEntity.getWidthTableFieldMap().get(widthTableForeignKey.foreignKey());
                widthTableFieldInfo.setForeignKeySourceTable(childEntity.getSourceTableName());
                widthTableFieldInfo.setForeignKeySourceColumn(childWidthTableFieldInfo.getSourceTableColumnName());
                widthTableFieldInfo.setForeignKeyWidthTableColumn(childWidthTableFieldInfo.getWidthTableColumnName());
            }
        }

        return widthTableEntityTree;

    }
}
