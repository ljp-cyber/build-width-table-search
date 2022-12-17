package com.shxex.bwts.processKafkaData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.utils.NameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 宽表更新到elasticsearch消费者
 *
 * @author ljp
 */
@Slf4j
@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class ToEsListener {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TableNameClassContext tableNameClassContext;

    public void handlerDateChange(Maxwell maxwell) {
        log.debug("开始处理到es的数据" + maxwell);

        Class<?> entityClass = tableNameClassContext.getEntityClass(maxwell.getTable());

        //获取改表的处理仓库类
        Class<?> repositoryClazz = tableNameClassContext.getRepositoryClass(maxwell.getTable());
        log.debug("处理仓库为：" + repositoryClazz);
        if (repositoryClazz == null) {
            return;
        }
        //处理增删改查
        ElasticsearchRepository repository = (ElasticsearchRepository) applicationContext.getBean(repositoryClazz);
        switch (maxwell.getType()) {
            case Maxwell.INSERT:
                repository.save(getEntity(entityClass, maxwell.getData()));
                break;
            case Maxwell.DELETE:
                repository.deleteById(maxwell.getData().get("id"));
                break;
            case Maxwell.UPDATE:
                repository.save(getEntity(entityClass, maxwell.getOld()));
                break;
        }
        Iterable all = repository.findAll();
        for (Object o : all) {
            System.out.println(o);
        }
    }

    private Object getEntity(Class<?> entityClass, Map data) {
        List<String> fields = new ArrayList<>();
        for (Object o : data.keySet()) {
            fields.add(o.toString());
        }
        for (String field : fields) {
            data.put(NameUtil.camelName(field), data.get(field));
        }
        Object value = objectMapper.convertValue(data, entityClass);
        return value;
    }
}
