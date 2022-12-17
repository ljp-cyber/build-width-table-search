package com.shxex.bwts.processKafkaData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.common.widthTableUpdate.WidthTableUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ljp
 */
@Slf4j
@Component
public class ToWidthTableListener {

    @Autowired
    private WidthTableUpdate widthTableUpdate;

    public void handlerDataChange(Maxwell maxwell) {
        log.debug("开始处理到宽表的数据" + maxwell);
        widthTableUpdate.update(maxwell.getTable(), maxwell.getOld(), maxwell.getData());
    }

}
