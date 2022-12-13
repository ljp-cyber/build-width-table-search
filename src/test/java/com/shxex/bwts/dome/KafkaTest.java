package com.shxex.bwts.dome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shxex.bwts.config.KafkaConfig;
import com.shxex.bwts.dome.entity.User;
import com.shxex.bwts.processKafkaData.Maxwell;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
            maxwell.setDatabase("my_website");
            maxwell.setTable("user_");

            User user = new User();
            user.setId(1L);
            user.setUserName("小娜");
            ObjectNode data = objectMapper.convertValue(user, ObjectNode.class);
            maxwell.setData(data);
            maxwell.setOld(data);

            ListenableFuture<SendResult<Integer, String>> send = kafkaTemplate.send("maxwell-common", maxwell.toString());

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
