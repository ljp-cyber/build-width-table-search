package com.shxex.bwts.dome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shxex.bwts.config.KafkaConfig;
import com.shxex.bwts.processKafkaData.Maxwell;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ljp
 */
public class KafkaTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(KafkaConfig.class);

        KafkaTemplate<Integer, String> kafkaTemplate = (KafkaTemplate<Integer, String>) ctx.getBean("kafkaTemplate");
        for (int i = 0; i < 10; i++) {
            Maxwell maxwell = new Maxwell();
            maxwell.setDatabase("bwts");
            maxwell.setTable("user_");

            Map user = new HashMap();
            user.put("id", 1L);
            user.put("userName", "小娜");
            maxwell.setData(user);
            maxwell.setOld(user);

            ListenableFuture<SendResult<Integer, String>> send = kafkaTemplate.send("bwts", maxwell.toString());

            send.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("fail");
                }

                @Override
                public void onSuccess(SendResult<Integer, String> integerStringSendResult) {
                    System.out.println("success");
                }
            });
        }
    }


}
