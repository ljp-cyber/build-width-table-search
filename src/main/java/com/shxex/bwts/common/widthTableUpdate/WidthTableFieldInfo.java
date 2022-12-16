package com.shxex.bwts.common.widthTableUpdate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 平面结构描述宽表字段间的联系
 */
@Data
public class WidthTableFieldInfo {

    public final static Integer FOREIGN_KEY_REL_NONE = 0;
    public final static Integer FOREIGN_KEY_REL_PARENT = 1;
    public final static Integer FOREIGN_KEY_REL_CHILD = 2;

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
     * 宽表 表名称
     */
    @ApiModelProperty("宽表 表名称")
    private String widthTableName;
    /**
     * 宽表 表字段名称
     */
    @ApiModelProperty("宽表 表字段名称")
    private String widthTableColumnName;

    /**
     * 外键关联情况 是否外键外键、外键关联父亲、外键关联父亲儿子
     * public final static Integer FOREIGN_KEY_REL_NONE = 0;
     * public final static Integer FOREIGN_KEY_REL_PARENT = 1;
     * public final static Integer FOREIGN_KEY_REL_CHILD = 2;
     */
    @ApiModelProperty("外键关联情况 不是外键、外键关联父亲、外键关联儿子")
    private Integer foreignKeyRel;

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
}
