package com.shxex.bwts.processKafkaData;

import com.shxex.bwts.common.middleTableUpdate.MiddleTableUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ljp
 */
@Slf4j
public class ToMiddleTableListener {

    @Autowired
    private MiddleTableUpdate middleTableUpdate;

    public void handlerDataChange(Maxwell maxwell) {
        middleTableUpdate.update(maxwell);
    }

}
