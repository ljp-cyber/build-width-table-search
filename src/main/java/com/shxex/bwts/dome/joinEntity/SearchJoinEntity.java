package com.shxex.bwts.dome.joinEntity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shxex.bwts.common.widthTableUpdate.WidthTableChild;
import com.shxex.bwts.common.widthTableUpdate.WidthTableEntity;
import com.shxex.bwts.common.widthTableUpdate.WidthTableField;
import com.shxex.bwts.common.widthTableUpdate.WidthTableForeignKey;

/**
 * id字段作为表的唯一标识，一般情况下都是不会变的，目前不考虑id会变的情况
 * 源表的id和宽表的id一致
 * 子实体的主键字段要出现在
 */
@TableName("search")
@WidthTableEntity(sourceTable = "user_", widthTable = "search")
public class SearchJoinEntity {

    @WidthTableField(sourceTablePrimaryKey = true, widthTablePrimaryKey = true)
    private Long id;

    @WidthTableField
    private String userName;

    @WidthTableChild
    private HobbySearchEntity hobbySearchEntity;

    @WidthTableEntity(sourceTable = "middle")
    public static class HobbySearchEntity {

        @WidthTableForeignKey(foreignKey = "id")
        @WidthTableField(sourceTableColumn = "id", sourceTablePrimaryKey = true)
        private Long hobbyId;

        @WidthTableField
        private String hobbyName;

    }
}
