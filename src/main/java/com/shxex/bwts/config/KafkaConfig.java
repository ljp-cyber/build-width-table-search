package com.shxex.bwts.config;

import com.shxex.bwts.processKafkaData.ToEsListener;
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
 * @author ljp
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    public static final String HOST = "47.119.122.155:9092";

    //配置Topic

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,HOST);
        configs.put("client.dns.lookup","use_all_dns_ips");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic("maxwell-common", 1, (short) 1);
    }

    //配置生产者Factory 配置Template

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<String,Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);
        props.put("acks", "all");
        props.put(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG,"use_all_dns_ips");
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
        return new KafkaTemplate<>(producerFactory());
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
        HashMap<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", HOST);
        props.put("client.dns.lookup","use_all_dns_ips");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        return props;
    }

    @Bean
    public ToEsListener simpleConsumerListener(){
        return new ToEsListener();
    }

}
