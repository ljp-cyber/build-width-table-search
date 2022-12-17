package com.shxex.bwts.common.widthTableUpdate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.utils.TableInfoUtil;
import com.shxex.bwts.processKafkaData.Maxwell;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 更新处理期，这里的代码比较硬核
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@AllArgsConstructor
@Slf4j
public class WidthTableUpdate {

    private WidthTableContext widthTableContext;
    private TableNameClassContext tableNameClassContext;
    private ObjectMapper objectMapper;

    public void update(Maxwell maxwell) {
        List<WidthTableEntityTree> list = widthTableContext.listByTableName(maxwell.getTable());
        if (list == null) {
            return;
        }
        //遍历所有影响到的关联实体
        for (WidthTableEntityTree widthTableEntityTree : list) {
            try {
                updateOne(widthTableEntityTree, maxwell);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void updateOne(WidthTableEntityTree widthTableEntityTree, Maxwell maxwell) {
        IService service = tableNameClassContext.getService(widthTableEntityTree.getWidthTableName());
        UpdateWrapper updateWrapper = new UpdateWrapper();

        Object primaryKeyValue = Optional.ofNullable(maxwell.getOld())
                .map(map -> map.get(widthTableEntityTree.getSourceTablePrimaryKey()))
                .orElse(maxwell.getData().get(widthTableEntityTree.getSourceTablePrimaryKey()));

        Map oldWidthTableData = new HashMap<>();
        if (Maxwell.DELETE.equals(maxwell.getType())) {
            service.removeById(primaryKeyValue.toString());
            return;
        }
        if (Maxwell.INSERT.equals(maxwell.getType()) && widthTableEntityTree.getParent() == null) {
            //处理更新的是根节点的情况，旧数据为空，说明是新增的数据，需要插入新数据
            Map insert = new HashMap<>();
            for (WidthTableFieldInfo widthTableFieldInfo : widthTableEntityTree.getWidthTableFiledList()) {
                String fieldName = TableInfoUtil.getColumnFieldNameFromCache(
                        widthTableFieldInfo.getWidthTableName(),
                        widthTableFieldInfo.getWidthTableColumnName());
                insert.put(fieldName, maxwell.getData().get(widthTableFieldInfo.getSourceTableColumnName()));
                oldWidthTableData.put(widthTableFieldInfo.getWidthTableColumnName(), maxwell.getData().get(widthTableFieldInfo.getSourceTableColumnName()));
            }
            service.save(objectMapper.convertValue(insert, service.getEntityClass()));
            updateWrapper.eq(widthTableEntityTree.getWidthTableColumnForSourcePrimaryKey(), primaryKeyValue);
        } else if (Maxwell.INSERT.equals(maxwell.getType()) && widthTableEntityTree.getWidthTableFiledForRelParent() != null) {
            //处理插入子表数据，而且是儿子关联父亲的情况
            WidthTableFieldInfo widthTableFieldInfo = widthTableEntityTree.getParent().getWidthTableFieldMap().get(widthTableEntityTree.getWidthTableFiledForRelParent().getForeignKeyWidthTableColumn());
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(widthTableFieldInfo.getWidthTableColumnName(), primaryKeyValue);
            queryWrapper.last("limit 1");
            oldWidthTableData = service.getMap(queryWrapper);
            if (oldWidthTableData == null) {
                oldWidthTableData = Collections.emptyMap();
            }
            updateWrapper.eq(widthTableFieldInfo.getWidthTableColumnName(), primaryKeyValue);
        } else {
            //旧数据不为空，说明宽表已经存在该条数据，查出来用
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(widthTableEntityTree.getWidthTableColumnForSourcePrimaryKey(), primaryKeyValue);
            queryWrapper.last("limit 1");
            oldWidthTableData = service.getMap(queryWrapper);
            if (oldWidthTableData == null) {
                oldWidthTableData = Collections.emptyMap();
            }
            updateWrapper.eq(widthTableEntityTree.getWidthTableColumnForSourcePrimaryKey(), primaryKeyValue);
        }

        recuseUpdate(updateWrapper, widthTableEntityTree, oldWidthTableData, maxwell.getData());
        if (StringUtils.isNotBlank(updateWrapper.getSqlSet())) {
            service.update(updateWrapper);
        }
    }

    /**
     * 父亲数据更新 和 对儿子的影响
     * 因为有些情况需要处理旧数据，所以这里新旧都需要传过来
     *
     * @param updateWrapper     对聚合表更新的Wrapper
     * @param curNode           父亲连接信息
     * @param widthTableOldData 旧数据，这个旧数据可以是宽表的所有信息
     * @param curNodeNewData    当前节点新数据
     */
    private void recuseUpdate(UpdateWrapper updateWrapper, WidthTableEntityTree curNode, Map widthTableOldData, Map curNodeNewData) {
        //遍历所有当前节点所有字段
        for (WidthTableFieldInfo widthTableFieldInfo : curNode.getWidthTableFiledList()) {
            Object oldValue = widthTableOldData.get(widthTableFieldInfo.getWidthTableColumnName());
            Object newValue = curNodeNewData.get(widthTableFieldInfo.getSourceTableColumnName());
            if (compareValue(oldValue, newValue)) {
                continue;
            }
            //新旧数据不同，需要更新
            updateWrapper.set(widthTableFieldInfo.getWidthTableColumnName(), newValue);

            //如果父亲关联儿子则需要递归更新，处理有外键的情况，递归处理儿子字段
            if (WidthTableFieldInfo.FOREIGN_KEY_REL_CHILD.equals(widthTableFieldInfo.getForeignKeyRel())) {
                //这里已经处理父亲关联儿子的情况，下面遍历儿子的时候就不用处理了

                //1、查出新儿子数据
                Map childNewData;
                if (newValue == null) {
                    //如果新外键为空则，新数据都是空的，不用查了
                    childNewData = Collections.emptyMap();
                } else {
                    //如果新外键不为空，则查出新数据
                    IService childService = tableNameClassContext.getService(widthTableFieldInfo.getForeignKeySourceTable());
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq(widthTableFieldInfo.getForeignKeySourceColumn(), curNodeNewData.get(widthTableFieldInfo.getForeignKeySourceColumn()));
                    queryWrapper.last("limit 1");
                    childNewData = childService.getMap(queryWrapper);
                    if (childNewData == null) {
                        childNewData = Collections.emptyMap();
                    }
                }

                //2、传递并递归更新
                recuseUpdate(updateWrapper, curNode.getChildrenMap().get(widthTableFieldInfo.getForeignKeySourceTable()), widthTableOldData, childNewData);
            }
        }

        //历遍所有儿子，处理有外键的情况
        for (WidthTableEntityTree child : curNode.getChildrenList()) {
            for (WidthTableFieldInfo childWidthTableFieldInfo : child.getWidthTableFiledList()) {
                if (!WidthTableFieldInfo.FOREIGN_KEY_REL_PARENT.equals(childWidthTableFieldInfo.getForeignKeyRel())) {
                    continue;
                }
                //如果儿子外键关联父亲字段，更新聚合表 对应儿子部分的信息，儿子的信息查询获取条件
                //上面已经处理父亲关联儿子的情况，这里只处理儿子关联父亲的情况

                Object oldForeignKeyValue = widthTableOldData.get(childWidthTableFieldInfo.getForeignKeyWidthTableColumn());
                Object newForeignKeyValue = curNodeNewData.get(childWidthTableFieldInfo.getForeignKeySourceColumn());
                if (compareValue(oldForeignKeyValue, newForeignKeyValue)) {
                    continue;
                }

                //1、查出新儿子数据
                Map childNewData;
                if (newForeignKeyValue == null) {
                    //如果新外键为空则，新数据都是空的，不用查了
                    childNewData = Collections.emptyMap();
                } else {
                    //如果新外键不为空，则查出新数据
                    IService childService = tableNameClassContext.getService(child.getSourceTableName());
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq(childWidthTableFieldInfo.getSourceTableColumnName(), curNodeNewData.get(childWidthTableFieldInfo.getSourceTableColumnName()));
                    queryWrapper.last("limit 1");
                    childNewData = childService.getMap(queryWrapper);
                    if (childNewData == null) {
                        childNewData = Collections.emptyMap();
                    }
                }
                //2、传递并递归更新
                recuseUpdate(updateWrapper, child, widthTableOldData, childNewData);
            }
        }
    }

    private boolean compareValue(Object oldValue, Object newValue) {
        if (oldValue == null && newValue == null) {
            //新旧同时为空不用处理
            return true;
        }
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            //新旧数据相同不用处理
            return true;
        }
        if (oldValue instanceof Number || newValue instanceof Number) {
            BigDecimal oldNumber = null;
            BigDecimal newNumber = null;
            try {
                oldNumber = NumberUtils.createBigDecimal(oldValue.toString());
                newNumber = NumberUtils.createBigDecimal(newValue.toString());
            } catch (Exception e) {
                return false;
            }
            return oldNumber.compareTo(newNumber) == 0;
        }
        return false;
    }

}
