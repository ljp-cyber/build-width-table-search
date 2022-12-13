package com.shxex.bwts.common.joinUpdate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 平面结构描述宽表字段间的联系
 */
@Data
public class JoinFieldInfo {

    /**
     * 事实表或维度表 表名称
     */
    @ApiModelProperty("事实表或维度表 表名称")
    private String tableName;
    /**
     * 事实表或维度表 表字段名称
     */
    @ApiModelProperty("事实表或维度表 表字段名称")
    private String columnName;
    /**
     * 事实表或维度表 表字段数据类型
     */
    @ApiModelProperty("事实表或维度表 表字段数据类型")
    private String jdbcType;

    /**
     * 事实表或维度表 实体字段名称
     */
    @ApiModelProperty("事实表或维度表 实体字段名称")
    private String fieldName;
    /**
     * 事实表或维度表 实体字段数据类型
     */
    @ApiModelProperty("事实表或维度表 实体字段数据类型")
    private String javaType;

    /**
     * 宽表 表名称
     */
    @ApiModelProperty("宽表 表名称")
    private String joinTableName;
    /**
     * 宽表 表字段名称
     */
    @ApiModelProperty("宽表 表字段名称")
    private String joinColumnName;
    /**
     * 宽表 表字段数据类型
     */
    @ApiModelProperty("宽表 表字段数据类型")
    private String joinJdbcType;

    /**
     * 宽表 实体字段名称
     */
    @ApiModelProperty("宽表 实体字段名称")
    private String joinFieldName;

    /**
     * 宽表 实体字段数据类型
     */
    @ApiModelProperty("宽表 实体字段数据类型")
    private String joinJavaType;

    /**
     * 关联事实表或维度表 表名称
     */
    @ApiModelProperty("关联事实表或维度表 表名称")
    private String foreignKeyTable;

    /**
     * 关联事实表或维度表 字段名称名称
     */
    @ApiModelProperty("关联事实表或维度表 表字段名称")
    private String foreignKeyColumn;

    /**
     * 关联宽表 表字段名称
     */
    @ApiModelProperty("关联宽表 表字段名称")
    private String foreignKeyJoinColumn;

    /**
     * 关联宽表 表字段名称
     */
    @ApiModelProperty("关联宽表 实体字段名称")
    private String foreignKeyJoinField;
}
