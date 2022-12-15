package com.shxex.bwts.dome.joinEntity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shxex.bwts.common.widthTableUpdate.WidthTableChild;
import com.shxex.bwts.common.widthTableUpdate.WidthTableEntity;
import com.shxex.bwts.common.widthTableUpdate.WidthTableField;
import com.shxex.bwts.common.widthTableUpdate.WidthTableForeignKey;

@TableName("search")
@WidthTableEntity(sourceTable = "user_", widthTable = "search")
public class SearchJoinEntity {

    @WidthTableField
    private Long id;

    @WidthTableField
    private String userName;

    @WidthTableChild
    private HobbySearchEntity hobbySearchEntity;

    @WidthTableEntity(sourceTable = "middle")
    public static class HobbySearchEntity {

        @WidthTableForeignKey(foreignKeyField = "id")
        @WidthTableField
        private Long id;

        @WidthTableField
        private String hobbyName;

    }
}
