package com.shxex.bwts.common.widthTableUpdate;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shxex.bwts.common.TableNameClassContext;
import lombok.AllArgsConstructor;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 更新处理期，这里的代码比较硬核
 */
@SuppressWarnings("rawtypes")
@AllArgsConstructor
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
        QueryChainWrapper queryChainWrapper = service.query();
        //如果更新的是根节点
        if (widthTableEntityTree.getParent() == null) {
            Object bean = new HashMap<>();
            if (ObjectUtils.isEmpty(oldDataMap)) {
                Map<String, Object> insert = new HashMap<>();
                for (WidthTableFieldInfo widthTableFieldInfo : widthTableEntityTree.getWidthTableFiledList()) {
                    insert.put(widthTableFieldInfo.getWidthEntityFieldName(), newDataMap.get(widthTableFieldInfo.getSourceTableColumnName()));
                }
                try {
                    bean = service.getEntityClass().newInstance();
                    BeanUtils.populate(bean, insert);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                service.save(bean);
            } else {
                for (WidthTableFieldInfo widthTableFieldInfo : widthTableEntityTree.getWidthTableFiledList()) {
                    if ("id".equals(widthTableFieldInfo.getSourceTableColumnName())) {
                        queryChainWrapper.eq(widthTableFieldInfo.getWidthTableColumnName(), Optional.ofNullable(oldDataMap)
                                .map(map -> map.get("id"))
                                .orElse(newDataMap.get("id")));
                        bean = queryChainWrapper.one();
                        break;
                    }
                }
            }
            Map oldData = new BeanMap(bean);
            updateChainWrapper.eq("id", Optional.ofNullable(oldDataMap)
                    .map(map -> map.get("id"))
                    .orElse(newDataMap.get("id")));
            recuseUpdate(updateChainWrapper, widthTableEntityTree, oldData, newDataMap);
        } else {
            //处理外键关联父亲的情况
            List<WidthTableFieldInfo> parentFiledList = widthTableEntityTree.getParent().getWidthTableFiledList();
            List<WidthTableFieldInfo> filedList = widthTableEntityTree.getWidthTableFiledList();
            a:
            for (WidthTableFieldInfo widthTableFieldInfo : filedList) {
                for (WidthTableFieldInfo parentField : parentFiledList) {
                    if (!isJoin(widthTableFieldInfo, parentField)) {
                        continue;
                    }
                    Object value = Optional.ofNullable(oldDataMap)
                            .map(map -> map.get(widthTableFieldInfo.getSourceTableColumnName()))
                            .orElse(newDataMap.get(widthTableFieldInfo.getSourceTableColumnName()));
                    updateChainWrapper.eq(parentField.getWidthTableColumnName(), value);
                    queryChainWrapper.eq(parentField.getWidthTableColumnName(), value);
                    Object bean = queryChainWrapper.one();
                    BeanMap oldData = new BeanMap(bean);
                    recuseUpdate(updateChainWrapper, widthTableEntityTree, oldData, newDataMap);
                    break a;
                }
            }
        }
        updateChainWrapper.update();
    }

    private boolean isJoin(WidthTableFieldInfo widthTableFieldInfo, WidthTableFieldInfo parentField) {
        if (isJoinParent(widthTableFieldInfo, parentField)) return true;
        if (isJoinParent(parentField, widthTableFieldInfo)) return true;
        return false;
    }

    private boolean isJoinParent(WidthTableFieldInfo widthTableFieldInfo, WidthTableFieldInfo parentField) {
        if (parentField.getSourceTableName().equals(widthTableFieldInfo.getForeignKeySourceTable())) {
            if (parentField.getSourceTableColumnName().equals(widthTableFieldInfo.getForeignKeySourceColumn())) {
                return true;
            }
        }
        return false;
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
        List<WidthTableFieldInfo> filedList = parent.getWidthTableFiledList();
        List<WidthTableEntityTree> children = parent.getChildrenList();
        //遍历所有父亲字段
        for (WidthTableFieldInfo widthTableFieldInfo : filedList) {
            Object oldColumn = oldData.get(widthTableFieldInfo.getWidthTableColumnName());
            Object newColumn = newData.get(widthTableFieldInfo.getSourceTableColumnName());
            if (oldColumn == null && newColumn == null) {
                //新旧同时为空不用处理
                continue;
            } else if (oldColumn != null && newColumn != null && oldColumn.equals(newColumn)) {
                //新旧数据相同不用处理
                continue;
            } else {
                //新旧数据不同，需要更新
                updateWrapper.set(widthTableFieldInfo.getWidthTableColumnName(), newColumn);
            }
            //如果关联儿子则需要递归更新，处理有外键的情况，递归处理儿子字段
            for (WidthTableEntityTree child : children) {
                //儿子部分关联表的服务
                IService childService = tableNameClassContext.getService(child.getSourceTableName());
                QueryChainWrapper queryChainWrapper = childService.query();

                List<WidthTableFieldInfo> childFiledList = child.getWidthTableFiledList();
                for (WidthTableFieldInfo childWidthTableFieldInfo : childFiledList) {
                    if (isJoinParent(widthTableFieldInfo, childWidthTableFieldInfo) && newColumn != null) {
                        //1、如果外键关联儿子，更新聚合表 对应儿子部分的信息，儿子的信息查询获取条件
                        queryChainWrapper.eq(widthTableFieldInfo.getForeignKeySourceColumn(), newColumn);
                    } else if (isJoinParent(childWidthTableFieldInfo, widthTableFieldInfo) && newColumn != null) {
                        //2、如果儿子外键关联父亲字段，更新聚合表 对应儿子部分的信息，儿子的信息查询获取条件
                        queryChainWrapper.eq(childWidthTableFieldInfo.getSourceTableColumnName(), newColumn);
                    } else {
                        //其余情况不用处理儿子
                        continue;
                    }
                    //获取儿子数据,如果新数据为空，则置空儿子
                    Map childNewData = null;
                    if (newColumn != null) {
                        List list = queryChainWrapper.list();
                        for (Object obj : list) {
                            BeanMap temp = new BeanMap(obj);
                            if (childNewData == null) {
                                childNewData = temp;
                            }
                        }
                    }
                    if (childNewData == null) {
                        childNewData = new HashMap<>();
                    }
                    //传递并递归更新
                    recuseUpdate(updateWrapper, child, oldData, childNewData);
                }
            }
        }
    }

}
