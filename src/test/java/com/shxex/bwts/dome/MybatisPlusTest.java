package com.shxex.bwts.dome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.widthTableUpdate.WidthTableContext;
import com.shxex.bwts.common.widthTableUpdate.WidthTableUpdate;
import com.shxex.bwts.config.MybatisPlusConfig;
import com.shxex.bwts.dome.joinEntity.SearchJoinEntity;
import com.shxex.bwts.processKafkaData.Maxwell;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ljp
 */
public class MybatisPlusTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MybatisPlusConfig.class);
        TableNameClassContext tableNameClassContext = new TableNameClassContext(
                new String[]{"com.shxex.bwts.dome.*"},
                new String[]{"com.shxex.bwts.dome.*"},
                new String[]{"com.shxex.bwts.dome.*"},
                new String[]{"com.shxex.bwts.dome.*"});

        WidthTableContext widthTableContext = new WidthTableContext();
        widthTableContext.parseJoinEntity(SearchJoinEntity.class);
        WidthTableUpdate widthTableUpdate = new WidthTableUpdate(widthTableContext, tableNameClassContext, objectMapper);

        Map user = new HashMap<>();
        user.put("id", 1L);
        user.put("userName", "小娜");
        Maxwell maxwell = new Maxwell();
        maxwell.setTable("user_");
        maxwell.setData(user);
        maxwell.setOld(user);

        widthTableUpdate.update(maxwell);

        ctx.close();
    }

}
