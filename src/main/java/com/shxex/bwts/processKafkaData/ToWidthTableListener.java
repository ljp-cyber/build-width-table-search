package com.shxex.bwts.processKafkaData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.widthTableUpdate.WidthTableUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author ljp
 */
@Slf4j
@Component
public class ToWidthTableListener {

    @Autowired
    private WidthTableUpdate widthTableUpdate;
    @Autowired
    private ObjectMapper objectMapper;

    public void handlerDataChange(Maxwell maxwell) {
        log.debug("开始处理到宽表的数据" + maxwell);
        widthTableUpdate.update(maxwell.getTable(),
                objectMapper.convertValue(maxwell.getData(), Map.class),
                objectMapper.convertValue(maxwell.getOld(), Map.class));
    }

}
