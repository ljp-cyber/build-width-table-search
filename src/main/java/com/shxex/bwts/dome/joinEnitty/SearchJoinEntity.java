package com.shxex.bwts.dome.joinEnitty;

import com.shxex.bwts.common.widthTableUpdate.WidthTableChild;
import com.shxex.bwts.common.widthTableUpdate.WidthTableEntity;
import com.shxex.bwts.common.widthTableUpdate.WidthTableField;
import com.shxex.bwts.common.widthTableUpdate.WidthTableForeignKey;

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
        private Long id;

        @WidthTableField
        private String hobbyName;

    }
}
