package com.shxex.bwts.dome.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 爱好
 * </p>
 *
 * @author ljp
 * @since 2022-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Hobby对象", description="爱好")
@TableName("hobby")
public class Hobby implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hobbyName;

    private String description;

    private Long userId;

    private String img;


}
