package com.shxex.bwts.processKafkaData;

import com.shxex.bwts.common.TableNameBeanContent;
import com.shxex.bwts.common.utils.JackJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataChangeListener {

    ToEsListener toEsListener;
    ToMiddleTableListener toMiddleTableListener;
    ToWidthTableListener toWidthTableListener;

    @KafkaListener(topics = "bwts")
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
            toEsListener.handlerDateChange(maxwell);
            toMiddleTableListener.handlerDataChange(maxwell);
            toWidthTableListener.handlerDataChange(maxwell);
            ack.acknowledge();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }

    }

}
