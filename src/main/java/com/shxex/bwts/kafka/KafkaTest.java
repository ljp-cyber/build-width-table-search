package com.shxex.bwts.kafka;

import com.alibaba.fastjson.JSON;
//import com.mta.search.elasticsearch.common.entity.BladeUser;
import com.shxex.bwts.kafka.config.KafkaConfig;
import com.shxex.bwts.kafka.entity.Maxwell;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

/**
 * @author mta09
 */
public class KafkaTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(KafkaConfig.class);

        KafkaTemplate<Integer, String> kafkaTemplate = (KafkaTemplate<Integer, String>) ctx.getBean("kafkaTemplate");
        for(long i=0L;i<10;i++){
            Maxwell maxwell= new Maxwell();
            maxwell.setDatabase("bladex");
            maxwell.setTable("blade_user");

//            BladeUser bladeUser = new BladeUser();
//            bladeUser.setId(i);
//            bladeUser.setAccount("account"+i);
//            bladeUser.setName("name"+i);
//            bladeUser.setRealName("realName"+i);
//
//            maxwell.setData(bladeUser);
            maxwell.setOld(null);

            ListenableFuture<SendResult<Integer, String>> send = kafkaTemplate.send("maxwell-common" ,JSON.toJSONString(maxwell));



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
