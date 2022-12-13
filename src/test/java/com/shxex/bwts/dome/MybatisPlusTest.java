package com.shxex.bwts.dome;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.joinUpdate.JoinEntityTreeContent;
import com.shxex.bwts.common.joinUpdate.JoinUpdate;
import com.shxex.bwts.common.utils.ScanUtil;
import com.shxex.bwts.config.MybatisPlusConfig;
import com.shxex.bwts.dome.entity.User;
import com.shxex.bwts.dome.joinEnitty.SearchJoinEntity;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/**
 * @author ljp
 */
public class MybatisPlusTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MybatisPlusConfig.class);

        Map<String, IService> tableServiceMap = ScanUtil.getTableServiceMap(ctx);
        JoinEntityTreeContent joinEntityTreeContent = new JoinEntityTreeContent();
        joinEntityTreeContent.parseJoinEntity(SearchJoinEntity.class);
        JoinUpdate joinUpdate = new JoinUpdate(tableServiceMap, joinEntityTreeContent);

        User user = new User();
        user.setId(1L);
        user.setUserName("小娜");
        Map data = objectMapper.convertValue(user, Map.class);

        joinUpdate.update("user_", data, data);

        ctx.close();
    }

}