package com.shxex.bwts.common.widthTableUpdate;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 树结构描述宽表字段间的联系
 * 源表即事实表火维度表
 */
@Data
public class WidthTableEntityTree {

    /**
     * 宽表 表名称
     */
    private String widthTableName;
    /**
     * 源表 表名称
     */
    private String sourceTableName;

    /**
     * 宽表 主键字段名
     */
    private String widthTablePrimaryKey;
    /**
     * 宽表 和源表对应的主键字段
     */
    private String widthTableColumnForSourcePrimaryKey;
    /**
     * 源表 主键字段名
     */
    private String sourceTablePrimaryKey;

    /**
     * 宽表 字段描述对象 列表
     */
    private List<WidthTableFieldInfo> widthTableFiledList;
    /**
     * 宽表 字段名称-字段描述对象 映射
     */
    private Map<String, WidthTableFieldInfo> widthTableFieldMap;

    /**
     * 关联父亲的子段
     */
    private WidthTableFieldInfo widthTableFiledForRelParent;
    /**
     * 父对象
     */
    private WidthTableEntityTree parent;

    /**
     * 宽表 子对象 列表
     */
    private List<WidthTableEntityTree> childrenList;
    /**
     * 源表名-子对象 映射
     */
    private Map<String, WidthTableEntityTree> childrenMap;

}
