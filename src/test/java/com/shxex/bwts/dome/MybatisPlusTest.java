package com.shxex.bwts.dome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.widthTableUpdate.WidthTableEntityTreeContent;
import com.shxex.bwts.common.widthTableUpdate.WidthTableUpdate;
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
        TableNameClassContext tableNameClassContext = new TableNameClassContext(new String[]{"com.shxex.bwts.dome.*"}, new String[]{"com.shxex.bwts.dome.*"}, new String[]{"com.shxex.bwts.dome.*"});

        WidthTableEntityTreeContent widthTableEntityTreeContent = new WidthTableEntityTreeContent();
        widthTableEntityTreeContent.parseJoinEntity(SearchJoinEntity.class);
        WidthTableUpdate widthTableUpdate = new WidthTableUpdate(widthTableEntityTreeContent, tableNameClassContext);

        User user = new User();
        user.setId(1L);
        user.setUserName("小娜");
        Map data = objectMapper.convertValue(user, Map.class);

        widthTableUpdate.update("user_", data, data);

        ctx.close();
    }

}
