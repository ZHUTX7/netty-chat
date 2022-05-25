//package com.zzz.pro;
//
//import java.util.Arrays;
//import java.util.Properties;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.junit.Before;
//import org.junit.Test;
//
//public class ConsumerTest {
//    private Properties props;
//
//    @Before
//    public void init() {
//        props = new Properties();
//        props.put("bootstrap.servers", "159.75.98.190:9092");
//        props.put("group.id", "testConsumer");
//        props.put("enable.auto.commit", "true");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("session.timeout.ms", "30000");
//        props.put("auto.offset.reset", "earliest");
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//    }
//
//    @Test
//    public void consume() {
//        System.out.println("begin consumer");
//        connectionKafka();
//        System.out.println("finish consumer");
//    }
//
//    @SuppressWarnings("resource")
//    public void connectionKafka() {
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Arrays.asList("10002", "10002"));
//
//        while (true) {
//            ConsumerRecords<String, String> records = consumer.poll(100);
//
//            for (ConsumerRecord<String, String> record : records) {
//                System.out.println("收到消息：" + record.value());
//            }
//        }
//    }
//}