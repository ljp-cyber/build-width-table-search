package com.shxex.bwts.dome.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author ljp
 * @since 2022-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Search对象", description="")
@TableName("search")
public class Search implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String userName;

    private Long hobbyId;

    private String hobbyName;


}
