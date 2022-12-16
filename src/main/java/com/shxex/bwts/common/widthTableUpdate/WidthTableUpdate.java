package com.shxex.bwts.common.widthTableUpdate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.utils.TableInfoUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;

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

    public void update(String tableName, Map oldDataMap, Map newDataMap) {
        List<WidthTableEntityTree> list = widthTableContext.listByTableName(tableName);
        if (list == null) {
            return;
        }
        //遍历所有影响到的关联实体
        for (WidthTableEntityTree widthTableEntityTree : list) {
            try {
                updateOne(widthTableEntityTree, oldDataMap, newDataMap);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void updateOne(WidthTableEntityTree widthTableEntityTree, Map oldDataMap, Map newDataMap) {
        IService service = tableNameClassContext.getService(widthTableEntityTree.getWidthTableName());
        UpdateChainWrapper updateChainWrapper = service.update();
        Object id = Optional.ofNullable(oldDataMap)
                .map(map -> map.get("id"))
                .orElse(newDataMap.get("id"));
        if (widthTableEntityTree.getParent() == null) {
            //处理更新的是根节点的情况
            Map oldWidthTableData = new HashMap<>();
            if (ObjectUtils.isEmpty(oldDataMap)) {
                //旧数据为空，说明是新增的数据，需要插入新数据
                Map insert = new HashMap<>();

                for (WidthTableFieldInfo widthTableFieldInfo : widthTableEntityTree.getWidthTableFiledList()) {
                    String fieldNameFromCache = TableInfoUtil.getColumnFieldNameFromCache(
                            widthTableFieldInfo.getWidthTableName(),
                            widthTableFieldInfo.getWidthTableColumnName());
                    insert.put(fieldNameFromCache, newDataMap.get(widthTableFieldInfo.getSourceTableColumnName()));
                    oldWidthTableData.put(widthTableFieldInfo.getWidthTableColumnName(), newDataMap.get(widthTableFieldInfo.getSourceTableColumnName()));
                }
                try {
                    Object insertEntity = service.getEntityClass().newInstance();
                    BeanUtils.populate(insertEntity, insert);
                    service.save(insertEntity);
                } catch (Exception exception) {
                    log.error(exception.getMessage(), exception);
                }
                updateChainWrapper.eq("id", oldWidthTableData.get("id"));
            } else {
                //旧数据不为空，说明宽表已经存在该条数据，查出来用
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq(widthTableEntityTree.getWidthTableColumnForSourcePrimaryKey(), id);
                queryWrapper.last("limit 1");
                oldWidthTableData = service.getMap(queryWrapper);
                updateChainWrapper.eq("id", id);
            }
            if (oldWidthTableData == null) {
                oldWidthTableData = new HashMap();
            }
            recuseUpdate(updateChainWrapper, widthTableEntityTree, oldWidthTableData, newDataMap);
        } else {
            updateChainWrapper.eq(widthTableEntityTree.getWidthTableColumnForSourcePrimaryKey(), id);
            recuseUpdate(updateChainWrapper, widthTableEntityTree, oldDataMap, newDataMap);
        }
        updateChainWrapper.update();
    }

    /**
     * 父亲数据更新 和 对儿子的影响
     * 因为有些情况需要处理旧数据，所以这里新旧都需要传过来
     *
     * @param updateWrapper 对聚合表更新的Wrapper
     * @param parent        父亲连接信息
     * @param oldData       旧数据，这个旧数据可以是聚合表的所有信息
     * @param newData       父亲新数据
     */
    private void recuseUpdate(UpdateChainWrapper updateWrapper, WidthTableEntityTree parent, Map oldData, Map newData) {
        //遍历所有父亲字段
        for (WidthTableFieldInfo widthTableFieldInfo : parent.getWidthTableFiledList()) {
            Object oldValue = oldData.get(widthTableFieldInfo.getWidthTableColumnName());
            Object newValue = newData.get(widthTableFieldInfo.getSourceTableColumnName());
            if (oldValue == null && newValue == null) {
                //新旧同时为空不用处理
                continue;
            } else if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
                //新旧数据相同不用处理
                continue;
            } else {
                //新旧数据不同，需要更新
                updateWrapper.set(widthTableFieldInfo.getWidthTableColumnName(), newValue);
            }

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
                    queryWrapper.eq(widthTableFieldInfo.getForeignKeySourceColumn(), newData.get(widthTableFieldInfo.getForeignKeySourceColumn()));
                    queryWrapper.last("limit 1");
                    childNewData = childService.getMap(queryWrapper);
                }
                if (childNewData == null) {
                    childNewData = Collections.emptyMap();
                }

                //2、传递并递归更新
                recuseUpdate(updateWrapper, parent.getChildrenMap().get(widthTableFieldInfo.getForeignKeySourceTable()), oldData, childNewData);
            }

            //历遍所有儿子，处理有外键的情况
            for (WidthTableEntityTree child : parent.getChildrenList()) {
                for (WidthTableFieldInfo childWidthTableFieldInfo : child.getWidthTableFiledList()) {
                    if (!WidthTableFieldInfo.FOREIGN_KEY_REL_PARENT.equals(childWidthTableFieldInfo.getForeignKeyRel())) {
                        continue;
                    }
                    //如果儿子外键关联父亲字段，更新聚合表 对应儿子部分的信息，儿子的信息查询获取条件
                    //上面已经处理父亲关联儿子的情况，这里只处理儿子关联父亲的情况

                    //1、查出新儿子数据
                    Map childNewData;
                    if (newValue == null) {
                        //如果新外键为空则，新数据都是空的，不用查了
                        childNewData = Collections.emptyMap();
                    } else {
                        //如果新外键不为空，则查出新数据
                        IService childService = tableNameClassContext.getService(child.getSourceTableName());
                        QueryWrapper queryWrapper = new QueryWrapper();
                        queryWrapper.eq(childWidthTableFieldInfo.getSourceTableColumnName(), newData.get(childWidthTableFieldInfo.getSourceTableColumnName()));
                        queryWrapper.last("limit 1");
                        childNewData = childService.getMap(queryWrapper);
                    }
                    if (childNewData == null) {
                        childNewData = Collections.emptyMap();
                    }
                    //2、传递并递归更新
                    recuseUpdate(updateWrapper, child, oldData, childNewData);
                }
            }
        }
    }

}
