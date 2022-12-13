package com.shxex.bwts.dome.esEntity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author ljp
 * @
 * @since 2022-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("search")
@Document(indexName = "search")
@ApiModel(value = "Search对象", description = "Search对象")
public class Search implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Id
    private Long id;

    @TableField("user_id")
    @Field(type = FieldType.Text)
    private Long userId;

    @TableField("user_name")
    @Field(type = FieldType.Text)
    private String userName;

    @TableField("hobby_id")
    @Field(type = FieldType.Text)
    private Long hobbyId;

    @TableField("hobby_name")
    @Field(type = FieldType.Text)
    private String hobbyName;


}

