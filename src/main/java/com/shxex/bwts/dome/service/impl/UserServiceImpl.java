package com.shxex.bwts.dome.service.impl;

import com.shxex.bwts.dome.entity.User;
import com.shxex.bwts.dome.mapper.UserMapper;
import com.shxex.bwts.dome.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljp
 * @since 2022-12-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
