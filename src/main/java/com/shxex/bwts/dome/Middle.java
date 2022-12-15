package com.shxex.bwts.dome;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shxex.bwts.common.middleTableUpdate.MiddleTableEntity;
import com.shxex.bwts.common.middleTableUpdate.MiddleTableField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@TableName("middle")
@MiddleTableEntity(sourceTable = "hobby", middleTable = "middle", groupColumn = "user_id")
public class Middle implements Serializable {

    @MiddleTableField(sourceColumn = "user_id", middleColumn = "id")
    private Long id;

    @MiddleTableField(sourceColumn = "hobby_name", middleColumn = "hobby_name")
    private String hobbyName;

}
