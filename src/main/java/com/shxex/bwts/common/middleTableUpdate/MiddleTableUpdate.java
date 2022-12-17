package com.shxex.bwts.common.middleTableUpdate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.utils.NameUtil;
import com.shxex.bwts.processKafkaData.Maxwell;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 更新处理期，这里的代码比较硬核
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@AllArgsConstructor
public class MiddleTableUpdate {

    private TableNameClassContext tableNameClassContext;
    private MiddleTableContext middleTableContext;

    public void update(Maxwell maxwell) {
        Set<String> tableNameSet = middleTableContext.setByTableName(maxwell.getTable());
        if (tableNameSet == null) {
            return;
        }
        for (String tableName : tableNameSet) {
            Class<?> entityClass = tableNameClassContext.getEntityClass(tableName);

            MiddleTableEntity middleTableEntity = entityClass.getAnnotation(MiddleTableEntity.class);
            if (middleTableEntity == null) {
                return;
            }

            if (!middleTableEntity.sourceTable().equals(maxwell.getTable())) {
                return;
            }

            Object groupColumnValue = maxwell.getData().get(middleTableEntity.groupColumn());
            if (groupColumnValue == null) {
                return;
            }

            IService sourceService = tableNameClassContext.getService(maxwell.getTable());
            IService middleService = tableNameClassContext.getService(middleTableEntity.middleTable());
            String groupColumnName = groupColumnValue.toString();

            Field[] fields = entityClass.getDeclaredFields();
            Map<String, String> aggregateQueryField = new HashMap<>();
            for (Field field : fields) {
                MiddleTableField middleTableField = field.getAnnotation(MiddleTableField.class);
                if (middleTableField == null) {
                    return;
                }
                aggregateQueryField.put(middleTableField.sourceColumn(), middleTableField.middleColumn());
            }

            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(middleTableEntity.groupColumn(), groupColumnName);
            queryWrapper.select(aggregateQueryField.keySet().toArray(new String[aggregateQueryField.keySet().size()]));
            List<Map<String, Object>> list = sourceService.listMaps(queryWrapper);

            if (middleService.getById(groupColumnName) == null) {
                Map<String, Object> save = new HashMap<>();
                for (String key : aggregateQueryField.keySet()) {
                    if (middleTableEntity.groupColumn().equals(key)) {
                        save.putIfAbsent("id", groupColumnName);
                        continue;
                    }
                    save.put(NameUtil.camelName(aggregateQueryField.get(key)), aggregateColumn(list, key));
                }
                try {
                    Object newInstance = middleService.getEntityClass().newInstance();
                    BeanUtils.populate(newInstance, save);
                    middleService.save(newInstance);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                UpdateChainWrapper updateChainWrapper = middleService.update();
                updateChainWrapper.eq("id", groupColumnName);
                for (String key : aggregateQueryField.keySet()) {
                    if (middleTableEntity.groupColumn().equals(key)) {
                        continue;
                    }
                    updateChainWrapper.set(aggregateQueryField.get(key), aggregateColumn(list, key));
                }
                updateChainWrapper.update();
            }
        }

    }

    private String aggregateColumn(List<Map<String, Object>> list, String key) {
        return list.stream()
                .filter(map -> map.get(key) != null)
                .map(map -> map.get(key).toString())
                .collect(Collectors.joining(","));
    }

}
