package com.shxex.bwts.common.middleTableUpdate;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;
import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.processKafkaData.Maxwell;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 更新处理期，这里的代码比较硬核
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@AllArgsConstructor
public class MiddleTableUpdate {

    private TableNameClassContext tableNameClassContext;

    public void update(Maxwell maxwell) {
        Class<?> entityClass = tableNameClassContext.getEntityClass(maxwell.getTable());

        MiddleTableEntity middleTableEntity = entityClass.getAnnotation(MiddleTableEntity.class);
        if (middleTableEntity == null) {
            return;
        }

        if (!middleTableEntity.sourceTable().equals(maxwell.getTable())) {
            return;
        }

        JsonNode groupColumnJsonNode = maxwell.getData().get(middleTableEntity.groupColumn());
        if (groupColumnJsonNode == null) {
            return;
        }

        IService sourceService = tableNameClassContext.getService(maxwell.getTable());
        IService middleService = tableNameClassContext.getService(middleTableEntity.middleTable());
        String groupColumnValue = groupColumnJsonNode.asText();

        Field[] fields = entityClass.getDeclaredFields();
        Map<String, String> aggregateQueryField = new HashMap<>();
        for (Field field : fields) {
            MiddleTableField middleTableField = field.getAnnotation(MiddleTableField.class);
            if (middleTableField == null) {
                return;
            }
            aggregateQueryField.put(middleTableField.sourceColumn(), middleTableField.middleColumn());
        }

        QueryChainWrapper queryChainWrapper = sourceService.query();
        queryChainWrapper.eq(middleTableEntity.groupColumn(), groupColumnValue);
        queryChainWrapper.select(aggregateQueryField.keySet().toArray(new String[aggregateQueryField.keySet().size()]));
        List<Map<String, Object>> list = sourceService.listMaps(queryChainWrapper);

        if (middleService.getById(groupColumnValue) == null) {
            Map<String, Object> save = new HashMap<>();
            for (String key : aggregateQueryField.keySet()) {
                if ("id".equals(key)) {
                    save.put("id", groupColumnValue);
                    continue;
                }
                save.put(aggregateQueryField.get(key), aggregateColumn(list, key));
            }
            try {
                Object newInstance = middleService.getEntityClass().newInstance();
                BeanUtils.populate(newInstance, save);
                sourceService.save(newInstance);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            UpdateChainWrapper updateChainWrapper = middleService.update();
            updateChainWrapper.eq("id", groupColumnValue);
            for (String key : aggregateQueryField.keySet()) {
                if ("id".equals(key)) {
                    continue;
                }
                updateChainWrapper.set(aggregateQueryField.get(key), aggregateColumn(list, key));
            }
            updateChainWrapper.update();
        }
    }

    private String aggregateColumn(List<Map<String, Object>> list, String key) {
        return list.stream()
                .filter(map -> map.get(key) != null)
                .map(map -> map.get(key).toString())
                .collect(Collectors.joining(","));
    }

}
