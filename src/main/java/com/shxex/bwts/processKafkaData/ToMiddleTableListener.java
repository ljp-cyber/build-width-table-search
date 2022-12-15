package com.shxex.bwts.processKafkaData;

import com.shxex.bwts.common.middleTableUpdate.MiddleTableUpdate;
import com.shxex.bwts.common.utils.ScanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author ljp
 */
@Slf4j
@Component
public class ToMiddleTableListener {

    @Autowired
    private MiddleTableUpdate middleTableUpdate;

    public void handlerDataChange(Maxwell maxwell) {
        middleTableUpdate.update(maxwell);
    }

}
