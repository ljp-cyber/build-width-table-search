package com.shxex.bwts.config;

import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.middleTableUpdate.MiddleTableUpdate;
import com.shxex.bwts.common.widthTableUpdate.WidthTableEntityTreeContent;
import com.shxex.bwts.common.widthTableUpdate.WidthTableUpdate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WidthTableConfig {

    @Bean
    public TableNameClassContext tableNameClassContext() {
        return new TableNameClassContext(new String[]{"com.shxex.bwts.dome.entity","com.shxex.bwts.dome.middleEntity"},
                new String[]{"com.shxex.bwts.dome.service","com.shxex.bwts.dome.service"},
                new String[]{"com.shxex.bwts.dome.esRepository"});
    }

    @Bean
    public WidthTableEntityTreeContent widthTableEntityTreeContent(TableNameClassContext tableNameClassContext) {
        WidthTableEntityTreeContent widthTableEntityTreeContent = new WidthTableEntityTreeContent();
        for (String tableName : tableNameClassContext.tableNameSet()) {
            widthTableEntityTreeContent.parseJoinEntity(tableNameClassContext.getEntityClass(tableName));
        }
        return widthTableEntityTreeContent;
    }

    @Bean
    public WidthTableUpdate widthTableUpdate(TableNameClassContext tableNameClassContext, WidthTableEntityTreeContent widthTableEntityTreeContent) {
        return new WidthTableUpdate(widthTableEntityTreeContent, tableNameClassContext);
    }

    @Bean
    public MiddleTableUpdate middleTableUpdate(TableNameClassContext tableNameClassContext) {
        return new MiddleTableUpdate(tableNameClassContext);
    }

}
