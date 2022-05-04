package com.shxex.bwts.kafka.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shxex.bwts.utils.DataProcessMaps;
import com.shxex.bwts.kafka.entity.Maxwell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Map;


/**
 * 单表更新到elasticsearch消费者
 * @author mta09
 */
@Slf4j
public class SingleTableConsumerListener {

    @Autowired
    private ApplicationContext applicationContext;

    @KafkaListener(topics = "maxwell-common")
    public void listen(byte[] src,Acknowledgment ack) {

        //调试代码
        log.debug(new String(src));
        log.debug(JSON.parse(new String(src)).toString());
        
        Maxwell maxwell = JSON.parseObject(src, Maxwell.class);

        //过滤不需要的消费
        if (DataProcessMaps.getTableEntityMap().get(maxwell.getTable()) == null) {
            log.info(maxwell.getDatabase() + ":" + maxwell.getTable() + ":不在消费范围内");
            ack.acknowledge();
            return;
        }

        try{
            maxwellProcess(maxwell);
            ack.acknowledge();
        }catch (Exception exception){
            exception.printStackTrace();
            onFail(maxwell,ack);
        }

    }

    private void maxwellProcess(Maxwell maxwell) {
        //提取新旧数据
        Class<?> curEntity = DataProcessMaps.getTableEntityMap().get(maxwell.getTable());
        JSONObject newJson = (JSONObject) maxwell.getData();
        JSONObject oldJson = (JSONObject) maxwell.getOld();

        //获取改表的处理仓库类
        Map<String, Class<?>> tableRepositoryMap = DataProcessMaps.getTableRepositoryMap();
//        log.debug("===============仓库==================");
//        log.debug(tableRepositoryMap.keySet().toString());
//        log.debug(tableRepositoryMap.values().toString());

        //处理增删改查
        Class<?> clazz = tableRepositoryMap.get(maxwell.getTable());
        log.debug("处理仓库为："+clazz);
        ElasticsearchRepository repository = (ElasticsearchRepository) applicationContext.getBean(clazz);
        switch(maxwell.getType()){
            case "insert":
                repository.save(JSON.parseObject(newJson.toString(), curEntity));
                break;
            case "delete":
                repository.deleteById(newJson.getLong("id"));
                break;
            case "update":
                if(new Integer(1).equals(oldJson.getInteger("is_deleted"))){
                    repository.deleteById(newJson.getLong("id"));
                }else{
                    repository.save(JSON.parseObject(newJson.toString(), curEntity));
                }
                break;
        }
    }

    private void onFail(Maxwell maxwell,Acknowledgment ack){
        return;
    }
}
