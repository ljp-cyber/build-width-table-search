package com.shxex.bwts.processKafkaData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shxex.bwts.common.TableNameBeanContent;
import com.shxex.bwts.common.utils.JackJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
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

    @KafkaListener(topics = "bwts-to-es")
    public void listen(byte[] src, Acknowledgment ack) {
        Maxwell maxwell = JackJsonUtil.parse(src, Maxwell.class);
        log.debug("开始处理到es的数据" + maxwell);

        //过滤不需要的消费
        if (TableNameBeanContent.getEntityClass(maxwell.getTable()) == null) {
            log.info(maxwell.getDatabase() + ":" + maxwell.getTable() + ":不在消费范围内");
            ack.acknowledge();
            return;
        }

        try {
            maxwellProcess(maxwell);
            ack.acknowledge();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }

    }

    private void maxwellProcess(Maxwell maxwell) {
        Class<?> entityClass = TableNameBeanContent.getEntityClass(maxwell.getTable());

        //提取新旧数据
        ObjectNode newJson = maxwell.getData();
        ObjectNode oldJson = maxwell.getOld();

        //获取改表的处理仓库类
        Class<?> repositoryClazz = TableNameBeanContent.getRepositoryClass(maxwell.getTable());
        log.debug("处理仓库为：" + repositoryClazz);

        //处理增删改查
        ElasticsearchRepository repository = (ElasticsearchRepository) applicationContext.getBean(repositoryClazz);
        switch (maxwell.getType()) {
            case "insert":
                repository.save(objectMapper.convertValue(newJson, entityClass));
                break;
            case "delete":
                repository.deleteById(newJson.get("id"));
                break;
            case "update":
                if (new Integer(1).equals(oldJson.get("is_deleted").asInt(0))) {
                    repository.deleteById(newJson.get("id"));
                } else {
                    repository.save(objectMapper.convertValue(newJson, entityClass));
                }
                break;
        }
    }
}
