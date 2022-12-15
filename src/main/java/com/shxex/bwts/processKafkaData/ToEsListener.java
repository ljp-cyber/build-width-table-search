package com.shxex.bwts.processKafkaData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shxex.bwts.common.TableNameClassContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;


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
        Class<?> entityClass = tableNameClassContext.getEntityClass(maxwell.getTable());

        //提取新旧数据
        ObjectNode newJson = maxwell.getData();
        ObjectNode oldJson = maxwell.getOld();

        //获取改表的处理仓库类
        Class<?> repositoryClazz = tableNameClassContext.getRepositoryClass(maxwell.getTable());
        log.debug("处理仓库为：" + repositoryClazz);

        //处理增删改查
        ElasticsearchRepository repository = (ElasticsearchRepository) applicationContext.getBean(repositoryClazz);
        switch (maxwell.getType()) {
            case Maxwell.INSERT:
                repository.save(objectMapper.convertValue(newJson, entityClass));
                break;
            case Maxwell.DELETE:
                repository.deleteById(newJson.get("id"));
                break;
            case Maxwell.UPDATE:
                if (new Integer(1).equals(oldJson.get("is_deleted").asInt(0))) {
                    repository.deleteById(newJson.get("id"));
                } else {
                    repository.save(objectMapper.convertValue(newJson, entityClass));
                }
                break;
        }
    }
}
