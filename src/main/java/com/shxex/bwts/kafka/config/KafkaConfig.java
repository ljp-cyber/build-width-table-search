package com.shxex.bwts.kafka.config;

import com.shxex.bwts.kafka.consumer.SingleTableConsumerListener;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mta09
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    public static final String HOST = "10.74.20.82:9092";

    //配置Topic

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,HOST);
        configs.put("client.dns.lookup","default");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic("maxwell-common", 3, (short) 1);
    }

    //配置生产者Factory 配置Template

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        return new DefaultKafkaProducerFactory<Integer,String>(producerConfigs());
    }
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<String,Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);
        props.put("acks", "all");
        props.put(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG,"default");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        return new KafkaTemplate<Integer, String>(producerFactory());
    }

    //消费者配置配置，ConsumerFactory

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer,String> kafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<Integer, String>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }


    @Bean
    public ConsumerFactory<Integer,String> consumerFactory(){
        return new DefaultKafkaConsumerFactory<Integer, String>(consumerConfigs());
    }

    @Bean
    public Map<String,Object> consumerConfigs(){
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("bootstrap.servers", HOST);
        props.put("client.dns.lookup","default");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        return props;
    }

    @Bean
    public SingleTableConsumerListener simpleConsumerListener(){
        return new SingleTableConsumerListener();
    }

}
