package com.shxex.bwts.common.joinUpdate;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 更新处理期，这里的代码比较硬核
 */
@SuppressWarnings("rawtypes")
public class JoinUpdate {

    private Map<String, IService> tableServiceMap;
    private JoinEntityTreeContent joinEntityTreeContent;

    public JoinUpdate(Map<String, IService> tableServiceMap, JoinEntityTreeContent joinEntityTreeContent) {
        this.joinEntityTreeContent = joinEntityTreeContent;
        this.tableServiceMap = tableServiceMap;
    }

    public void update(String tableName, Map oldDataMap, Map newDataMap) {
        List<JoinEntityTree> list = joinEntityTreeContent.listByTableName(tableName);
        //遍历所有影响到的关联实体
        for (JoinEntityTree joinEntityTree : list) {
            try {
                updateOne(joinEntityTree, oldDataMap, newDataMap);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void updateOne(JoinEntityTree joinEntityTree, Map oldDataMap, Map newDataMap) {
        IService service = tableServiceMap.get(joinEntityTree.getJoinTableName());
        UpdateChainWrapper updateChainWrapper = service.update();
        QueryChainWrapper queryChainWrapper = service.query();
        //如果更新的是根节点
        if (joinEntityTree.getParent() == null) {
            Object bean = new HashMap<>();
            if (ObjectUtils.isEmpty(oldDataMap)) {
                Map<String, Object> insert = new HashMap<>();
                for (JoinFieldInfo joinFieldInfo : joinEntityTree.getFiledList()) {
                    insert.put(joinFieldInfo.getJoinFieldName(), newDataMap.get(joinFieldInfo.getFieldName()));
                }
                try {
                    bean = service.getEntityClass().newInstance();
                    BeanUtils.populate(bean, insert);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                service.save(bean);
            } else {
                for (JoinFieldInfo joinFieldInfo : joinEntityTree.getFiledList()) {
                    if ("id".equals(joinFieldInfo.getColumnName())) {
                        queryChainWrapper.eq(joinFieldInfo.getJoinColumnName(), oldDataMap.get("id"));
                        bean = queryChainWrapper.one();
                        break;
                    }
                }
            }
            Map oldData = new BeanMap(bean);
            updateChainWrapper.eq("id", oldData.get("id"));
            recuseUpdate(updateChainWrapper, joinEntityTree, oldData, newDataMap);
        } else {
            //处理外键关联父亲的情况
            List<JoinFieldInfo> parentFiledList = joinEntityTree.getParent().getFiledList();
            List<JoinFieldInfo> filedList = joinEntityTree.getFiledList();
            a:
            for (JoinFieldInfo joinFieldInfo : filedList) {
                for (JoinFieldInfo parentField : parentFiledList) {
                    if (!isJoin(joinFieldInfo, parentField)) {
                        continue;
                    }
                    if (oldDataMap == null) {
                        updateChainWrapper.eq(parentField.getJoinColumnName(), newDataMap.get(joinFieldInfo.getFieldName()));
                        queryChainWrapper.eq(parentField.getJoinColumnName(), newDataMap.get(joinFieldInfo.getFieldName()));
                    } else {
                        updateChainWrapper.eq(parentField.getJoinColumnName(), oldDataMap.get(joinFieldInfo.getFieldName()));
                        queryChainWrapper.eq(parentField.getJoinColumnName(), oldDataMap.get(joinFieldInfo.getFieldName()));
                    }
                    Object bean = queryChainWrapper.one();
                    recuseUpdate(updateChainWrapper, joinEntityTree, new BeanMap(bean), newDataMap);
                    break a;
                }
            }
        }
        updateChainWrapper.update();
    }

    private boolean isJoin(JoinFieldInfo joinFieldInfo, JoinFieldInfo parentField) {
        if (isJoinParent(joinFieldInfo, parentField)) return true;
        if (isJoinParent(parentField, joinFieldInfo)) return true;
        return false;
    }

    private boolean isJoinParent(JoinFieldInfo joinFieldInfo, JoinFieldInfo parentField) {
        if (parentField.getTableName().equals(joinFieldInfo.getForeignKeyTable())) {
            if (parentField.getColumnName().equals(joinFieldInfo.getForeignKeyColumn())) {
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
    private void recuseUpdate(UpdateChainWrapper updateWrapper, JoinEntityTree parent, Map oldData, Map newData) {
        List<JoinFieldInfo> filedList = parent.getFiledList();
        List<JoinEntityTree> children = parent.getChildrenList();
        //遍历所有父亲字段
        for (JoinFieldInfo joinFieldInfo : filedList) {
            Object oldColumn = oldData.get(joinFieldInfo.getJoinFieldName());
            Object newColumn = newData.get(joinFieldInfo.getFieldName());
            if (oldColumn == null && newColumn == null) {
                //新旧同时为空不用处理
                continue;
            } else if (oldColumn != null && newColumn != null && oldColumn.equals(newColumn)) {
                //新旧数据相同不用处理
                continue;
            } else {
                //新旧数据不同，需要更新
                updateWrapper.set(joinFieldInfo.getJoinColumnName(), newColumn);
            }
            //如果关联儿子则需要递归更新，处理有外键的情况，递归处理儿子字段
            for (JoinEntityTree child : children) {
                //儿子部分关联表的服务
                IService childService = tableServiceMap.get(child.getTableName());
                QueryChainWrapper queryChainWrapper = childService.query();

                List<JoinFieldInfo> childFiledList = child.getFiledList();
                for (JoinFieldInfo childJoinFieldInfo : childFiledList) {
                    if (isJoinParent(joinFieldInfo, childJoinFieldInfo) && newColumn != null) {
                        //1、如果外键关联儿子，更新聚合表 对应儿子部分的信息，儿子的信息查询获取条件
                        queryChainWrapper.eq(joinFieldInfo.getForeignKeyColumn(), newColumn);
                    } else if (isJoinParent(childJoinFieldInfo, joinFieldInfo) && newColumn != null) {
                        //2、如果儿子外键关联父亲字段，更新聚合表 对应儿子部分的信息，儿子的信息查询获取条件
                        queryChainWrapper.eq(childJoinFieldInfo.getColumnName(), newColumn);
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
