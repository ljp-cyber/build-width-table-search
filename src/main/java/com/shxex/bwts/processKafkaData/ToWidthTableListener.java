package com.shxex.bwts.processKafkaData;

import com.shxex.bwts.common.TableNameBeanContent;
import com.shxex.bwts.common.utils.JackJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * @author ljp
 */
@Slf4j
public class ToWidthTableListener {

    @KafkaListener(topics = "bwts-to-width-table")
    public void listen(byte[] src, Acknowledgment ack) {
        Maxwell maxwell = JackJsonUtil.parse(src, Maxwell.class);
        log.debug("开始处理到宽表的数据" + maxwell);

        //过滤不需要的消费
        if (TableNameBeanContent.getEntityClass(maxwell.getTable()) == null) {
            log.info(maxwell.getDatabase() + ":" + maxwell.getTable() + ":不在消费范围内");
            ack.acknowledge();
            return;
        }

        try {
            ack.acknowledge();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }

    }

}
