package com.shxex.bwts.dome.entity;

import java.time.LocalDate;
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
@ApiModel(value="User对象", description="")
@TableName("user_")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String userName;

    private String occupation;

    private Long mobileNumber;

    private String address;

    private String email;

    private String education;

    private LocalDate birth;

    private String icon;

    private String nickName;

    private String userPassword;

    private String wx;

    private String wxImg;

    @ApiModelProperty(value = "用户角色")
    private String role;

    @ApiModelProperty(value = "用户状态，默认开启")
    private String state;


}
