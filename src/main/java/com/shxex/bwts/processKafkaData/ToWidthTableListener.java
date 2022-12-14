package com.shxex.bwts.processKafkaData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.widthTableUpdate.WidthTableUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author ljp
 */
@Slf4j
public class ToWidthTableListener {

    @Autowired
    private WidthTableUpdate widthTableUpdate;
    @Autowired
    private ObjectMapper objectMapper;

    public void handlerDataChange(Maxwell maxwell) {
        widthTableUpdate.update(maxwell.getTable(),
                objectMapper.convertValue(maxwell.getData(), Map.class),
                objectMapper.convertValue(maxwell.getOld(), Map.class));
    }

}
