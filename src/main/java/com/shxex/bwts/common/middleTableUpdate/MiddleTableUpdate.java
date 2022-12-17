package com.shxex.bwts.common.middleTableUpdate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.utils.NameUtil;
import com.shxex.bwts.processKafkaData.Maxwell;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;

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
    private ObjectMapper objectMapper;

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

            Object groupColumnObject = maxwell.getData().get(middleTableEntity.groupColumn());
            if (groupColumnObject == null) {
                return;
            }

            IService sourceService = tableNameClassContext.getService(maxwell.getTable());
            IService middleService = tableNameClassContext.getService(middleTableEntity.middleTable());
            String groupColumnValue = groupColumnObject.toString();

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
            queryWrapper.eq(middleTableEntity.groupColumn(), groupColumnValue);
            queryWrapper.select(aggregateQueryField.keySet().toArray(new String[aggregateQueryField.keySet().size()]));
            List<Map<String, Object>> list = sourceService.listMaps(queryWrapper);

            if(ObjectUtils.isEmpty(list)){
                middleService.removeById(groupColumnValue);
                continue;
            }

            if (middleService.getById(groupColumnValue) == null) {
                Map<String, Object> save = new HashMap<>();
                for (String key : aggregateQueryField.keySet()) {
                    if (middleTableEntity.groupColumn().equals(key)) {
                        save.putIfAbsent("id", groupColumnValue);
                        continue;
                    }
                    save.put(NameUtil.camelName(aggregateQueryField.get(key)), aggregateColumn(list, key));
                }
                middleService.save(objectMapper.convertValue(save,middleService.getEntityClass()));
            } else {
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.eq("id", groupColumnValue);
                for (String key : aggregateQueryField.keySet()) {
                    if (middleTableEntity.groupColumn().equals(key)) {
                        continue;
                    }
                    updateWrapper.set(aggregateQueryField.get(key), aggregateColumn(list, key));
                }
                middleService.update(updateWrapper);
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
