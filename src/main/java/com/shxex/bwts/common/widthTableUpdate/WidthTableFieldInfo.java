package com.shxex.bwts.common.widthTableUpdate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 平面结构描述宽表字段间的联系
 */
@Data
public class WidthTableFieldInfo {

    /**
     * 事实表或维度表 表名称
     */
    @ApiModelProperty("事实表或维度表 表名称")
    private String sourceTableName;
    /**
     * 事实表或维度表 表字段名称
     */
    @ApiModelProperty("事实表或维度表 表字段名称")
    private String sourceTableColumnName;

    /**
     * 事实表或维度表 实体字段名称
     */
    @ApiModelProperty("事实表或维度表 实体字段名称")
    private String sourceEntityFieldName;

    /**
     * 宽表 表名称
     */
    @ApiModelProperty("宽表 表名称")
    private String widthTableName;
    /**
     * 宽表 表字段名称
     */
    @ApiModelProperty("宽表 表字段名称")
    private String widthColumnName;

    /**
     * 宽表 实体字段名称
     */
    @ApiModelProperty("宽表 实体字段名称")
    private String widthEntityFieldName;

    /**
     * 关联事实表或维度表 表名称
     */
    @ApiModelProperty("关联事实表或维度表 表名称")
    private String foreignKeySourceTable;

    /**
     * 关联事实表或维度表 字段名称名称
     */
    @ApiModelProperty("关联事实表或维度表 表字段名称")
    private String foreignKeySourceColumn;

    /**
     * 关联宽表 表字段名称
     */
    @ApiModelProperty("关联宽表 表字段名称")
    private String foreignKeyWidthTableColumn;

    /**
     * 关联宽表 表字段名称
     */
    @ApiModelProperty("关联宽表 实体字段名称")
    private String foreignKeyWidthEntityField;
}
