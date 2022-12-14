package com.shxex.bwts.common.widthTableUpdate;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 树结构描述宽表字段间的联系
 */
@Data
public class WidthTableEntityTree {

    /**
     * 宽表 表名称
     */
    private String widthTableName;
    /**
     * 事实表或维度表 表名称
     */
    private String sourceTableName;

    /**
     * 宽表 字段描述对象 列表
     */
    private List<WidthTableFieldInfo> widthTableFiledList;
    /**
     * 宽表 字段名称-字段描述对象 映射
     */
    private Map<String, WidthTableFieldInfo> widthTableFieldMap;

    /**
     * 父对象
     */
    private WidthTableEntityTree parent;

    /**
     * 子对象列表
     */
    private List<WidthTableEntityTree> childrenList;
    /**
     * 虚拟字段名称-子对象 映射
     */
    private Map<String, WidthTableEntityTree> childrenMap;
}
