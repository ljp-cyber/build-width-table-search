package com.shxex.bwts.config;

import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.middleTableUpdate.MiddleTableContext;
import com.shxex.bwts.common.middleTableUpdate.MiddleTableUpdate;
import com.shxex.bwts.common.widthTableUpdate.WidthTableContext;
import com.shxex.bwts.common.widthTableUpdate.WidthTableUpdate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TableConfig {

    @Bean
    public TableNameClassContext tableNameClassContext() {
        return new TableNameClassContext(
                new String[]{"com.shxex.bwts.dome.joinEntity"},
                new String[]{"com.shxex.bwts.dome.entity"},
                new String[]{"com.shxex.bwts.dome.service"},
                new String[]{"com.shxex.bwts.dome.esRepository"});
    }

    @Bean
    public WidthTableContext widthTableContext(TableNameClassContext tableNameClassContext) {
        WidthTableContext widthTableContext = new WidthTableContext();
        for (String tableName : tableNameClassContext.buildWidthTableNameSet()) {
            widthTableContext.parseJoinEntity(tableNameClassContext.getBuildWidthTableEntityClass(tableName));
        }
        return widthTableContext;
    }

    @Bean
    public MiddleTableContext middleTableContext(TableNameClassContext tableNameClassContext) {
        MiddleTableContext middleTableContext = new MiddleTableContext();
        for (String tableName : tableNameClassContext.tableNameSet()) {
            middleTableContext.parseJoinEntity(tableNameClassContext.getEntityClass(tableName));
        }
        return middleTableContext;
    }


    @Bean
    public WidthTableUpdate widthTableUpdate(TableNameClassContext tableNameClassContext, WidthTableContext widthTableContext) {
        return new WidthTableUpdate(widthTableContext, tableNameClassContext);
    }

    @Bean
    public MiddleTableUpdate middleTableUpdate(TableNameClassContext tableNameClassContext,MiddleTableContext middleTableContext) {
        return new MiddleTableUpdate(tableNameClassContext, middleTableContext);
    }

}
