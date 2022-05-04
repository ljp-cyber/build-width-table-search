package com.shxex.bwts.compone;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shxex.bwts.tools.utils.BeanUtil;
import com.shxex.bwts.tools.utils.Func;
import com.shxex.bwts.utils.NameUtil;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 更新处理期，这里的代码比较硬核
 */
@SuppressWarnings("rawtypes")
public class JoinUpdate {

	private List<JoinEntityTree> rootJoinEntityTreeList = new ArrayList<>();

	private Map<String, List<JoinEntityTree>> allJoinEntityMap = new HashMap<>();

	private Map<String, IService> serviceMap;

	public JoinUpdate(Map<String, IService> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public JoinEntityTree parseJoinEntity(Class<?> aClass) {
		JoinEntityTree joinEntityTree = recuseParseJoinEntity(aClass, null);
		rootJoinEntityTreeList.add(joinEntityTree);
		scanJoinEntityRecuse(Collections.singletonList(joinEntityTree));
		return joinEntityTree;
	}

	private JoinEntityTree recuseParseJoinEntity(Class<?> aClass, JoinEntityTree parent) {
		JoinEntity annotation = aClass.getAnnotation(JoinEntity.class);
		if (annotation == null) {
			return null;
		}

		JoinEntityTree joinEntityTree = new JoinEntityTree();
		joinEntityTree.setParent(parent);

		String table = annotation.table();
		String joinTable = annotation.joinTable();
		joinEntityTree.setTableName(table);
		joinEntityTree.setJoinTableName(parent != null ? parent.getJoinTableName() : joinTable);


		List<JoinFieldInfo> filedList = new ArrayList<>();
		Map<String, JoinFieldInfo> fieldMap = new HashMap<>();
		joinEntityTree.setFiledList(filedList);
		joinEntityTree.setFieldMap(fieldMap);

		List<JoinEntityTree> childrenList = new ArrayList<>();
		Map<String, JoinEntityTree> childrenMap = new HashMap<>();
		joinEntityTree.setChildrenList(childrenList);
		joinEntityTree.setChildrenMap(childrenMap);

		Field[] fields = aClass.getDeclaredFields();
		for (Field field : fields) {
			JoinField updateField = field.getAnnotation(JoinField.class);
			JoinFieldInfo joinFieldInfo = new JoinFieldInfo();
			joinFieldInfo.setTableName(joinEntityTree.getTableName());
			joinFieldInfo.setColumnName(updateField != null ? updateField.column() : NameUtil.underscoreName(field.getName()));
			joinFieldInfo.setJdbcType("");//TODO 这个有什么用？

			joinFieldInfo.setFieldName(updateField != null ? updateField.field() : field.getName());
			joinFieldInfo.setJavaType(field.getType().getName());

			joinFieldInfo.setJoinTableName(joinEntityTree.getJoinTableName());
			joinFieldInfo.setJoinColumnName(NameUtil.underscoreName(field.getName()));
			joinFieldInfo.setJoinJdbcType("");//TODO 这个有什么用？

			joinFieldInfo.setJoinFieldName(field.getName());
			joinFieldInfo.setJoinJavaType("");//TODO 这个有什么用？应该是用来辅助生成代码

			filedList.add(joinFieldInfo);
			fieldMap.put(field.getName(), joinFieldInfo);
		}

		for (Field field : fields) {
			Class<?> fieldType = field.getType();
			JoinEntityTree childJoinEntityTree = recuseParseJoinEntity(fieldType, joinEntityTree);
			if (childJoinEntityTree != null) {
				childrenList.add(childJoinEntityTree);
				childrenMap.put(field.getName(), childJoinEntityTree);
			}
		}


		for (Field field : fields) {
			JoinFieldInfo joinFieldInfo = fieldMap.get(field.getName());
			JoinForeignKey joinForeignKey = field.getAnnotation(JoinForeignKey.class);
			if (joinForeignKey == null) {
				continue;
			}
			String foreignKeyChild = joinForeignKey.foreignKeyChild();
			if (Func.isBlank(foreignKeyChild)) {
				//foreignKeyChild 为空，默认关联父亲
				JoinFieldInfo parentField = parent.getFieldMap().get(joinForeignKey.foreignKeyField());
				joinFieldInfo.setForeignKeyTable(parentField.getTableName());
				joinFieldInfo.setForeignKeyColumn(parentField.getColumnName());
				joinFieldInfo.setForeignKeyJoinField(parentField.getJoinFieldName());
				joinFieldInfo.setForeignKeyJoinColumn(parentField.getJoinColumnName());
			} else {
				//foreignKeyChild 不为空，关联指定儿子
				JoinEntityTree childEntity = childrenMap.get(foreignKeyChild);
				JoinFieldInfo childJoinFieldInfo = childEntity.getFieldMap().get(joinForeignKey.foreignKeyField());
				joinFieldInfo.setForeignKeyTable(childEntity.getTableName());
				joinFieldInfo.setForeignKeyColumn(childJoinFieldInfo.getColumnName());
				joinFieldInfo.setForeignKeyJoinField(childJoinFieldInfo.getJoinFieldName());
				joinFieldInfo.setForeignKeyJoinColumn(childJoinFieldInfo.getJoinColumnName());
			}
		}

		return joinEntityTree;

	}

	private void scanJoinEntityRecuse(List<JoinEntityTree> list) {
		if (list == null) {
			return;
		}
		for (JoinEntityTree joinEntityTree : list) {
			allJoinEntityMap.putIfAbsent(joinEntityTree.getTableName(), new ArrayList<>());
			allJoinEntityMap.get(joinEntityTree.getTableName()).add(joinEntityTree);
			scanJoinEntityRecuse(joinEntityTree.getChildrenList());
		}
	}

	public void update(String tableName, Map<String, Object> oldDataMap, Map<String, Object> newDataMap) {
		List<JoinEntityTree> list = allJoinEntityMap.get(tableName);
		//遍历所有影响到的关联实体
		for (JoinEntityTree joinEntityTree : list) {
			try {
				updateOne(joinEntityTree, oldDataMap, newDataMap);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	private void updateOne(JoinEntityTree joinEntityTree, Map<String, Object> oldDataMap, Map<String, Object> newDataMap) {
		IService service = serviceMap.get(joinEntityTree.getJoinTableName());
		UpdateChainWrapper updateChainWrapper = service.update();
		QueryChainWrapper queryChainWrapper = service.query();
		//如果更新的是根节点
		if (joinEntityTree.getParent() == null) {
			Object bean = new HashMap<>();
			if (Func.isEmpty(oldDataMap)) {
				Map<String, Object> insert = new HashMap<>();
				for (JoinFieldInfo joinFieldInfo : joinEntityTree.getFiledList()) {
					insert.put(joinFieldInfo.getJoinFieldName(), newDataMap.get(joinFieldInfo.getFieldName()));
				}
				bean = BeanUtil.toBean(insert, service.getEntityClass());
				service.save(bean);
			} else {
				for (JoinFieldInfo joinFieldInfo : joinEntityTree.getFiledList()) {
					if ("id".equals(joinFieldInfo.getColumnName())) {
						queryChainWrapper.eq(joinFieldInfo.getJoinFieldName(), oldDataMap.get("id"));
						bean = queryChainWrapper.one();
						break;
					}
				}
			}
			Map<String, Object> oldData = BeanUtil.toMap(bean);
			updateChainWrapper.eq("id", oldData.get("id"));
			recuseUpdate(updateChainWrapper, joinEntityTree, oldData, newDataMap);
		}else{
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
					recuseUpdate(updateChainWrapper, joinEntityTree, BeanUtil.toMap(bean), newDataMap);
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
	private void recuseUpdate(UpdateChainWrapper updateWrapper, JoinEntityTree parent, Map<String, Object> oldData, Map<String, Object> newData) {
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
				IService childService = serviceMap.get(child.getTableName());
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
					Map<String, Object> childNewData = null;
					if (newColumn != null) {
						BeanUtil.toMap(queryChainWrapper.one());
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
