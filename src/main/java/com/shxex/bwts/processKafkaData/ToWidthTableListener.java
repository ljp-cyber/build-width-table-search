package com.shxex.bwts.processKafkaData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.TableNameBeanContent;
import com.shxex.bwts.common.joinUpdate.JoinUpdate;
import com.shxex.bwts.common.utils.JackJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Map;

/**
 * @author ljp
 */
@Slf4j
public class ToWidthTableListener {

    @Autowired
    private JoinUpdate joinUpdate;
    @Autowired
    private ObjectMapper objectMapper;

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
            joinUpdate.update(maxwell.getTable(),
                    objectMapper.convertValue(maxwell.getData(), Map.class),
                    objectMapper.convertValue(maxwell.getOld(), Map.class));
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
        ack.acknowledge();

    }

}
