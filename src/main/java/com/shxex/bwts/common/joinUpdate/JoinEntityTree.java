package com.shxex.bwts.common.joinUpdate;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 树结构描述宽表字段间的联系
 */
@Data
public class JoinEntityTree {

    /**
     * 宽表 表名称
     */
    private String joinTableName;
    /**
     * 事实表或维度表 表名称
     */
    private String tableName;

    /**
     * 宽表 字段描述对象 列表
     */
    private List<JoinFieldInfo> filedList;
    /**
     * 宽表 字段名称-字段描述对象 映射
     */
    private Map<String, JoinFieldInfo> fieldMap;

    /**
     * 父对象
     */
    private JoinEntityTree parent;

    /**
     * 子对象列表
     */
    private List<JoinEntityTree> childrenList;
    /**
     * 虚拟字段名称-字段下 映射
     */
    private Map<String, JoinEntityTree> childrenMap;
}
