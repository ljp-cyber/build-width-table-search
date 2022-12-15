package com.shxex.bwts.processKafkaData;

import com.shxex.bwts.common.TableNameClassContext;
import com.shxex.bwts.common.utils.JackJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataChangeListener {

    @Autowired
    private ToEsListener toEsListener;
    @Autowired
    private ToMiddleTableListener toMiddleTableListener;
    @Autowired
    private ToWidthTableListener toWidthTableListener;
    @Autowired
    private TableNameClassContext tableNameClassContext;

    @KafkaListener(topics = "bwts")
    public void listen(byte[] src) {
        Maxwell maxwell = JackJsonUtil.parse(src, Maxwell.class);
        log.debug("开始处理到es的数据" + maxwell);

        //过滤不需要的消费
        if (tableNameClassContext.getEntityClass(maxwell.getTable()) == null) {
            log.info(maxwell.getDatabase() + ":" + maxwell.getTable() + ":不在消费范围内");
//            ack.acknowledge();
            return;
        }

        try {
            toEsListener.handlerDateChange(maxwell);
            toMiddleTableListener.handlerDataChange(maxwell);
            toWidthTableListener.handlerDataChange(maxwell);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }

//        ack.acknowledge();
    }

}
