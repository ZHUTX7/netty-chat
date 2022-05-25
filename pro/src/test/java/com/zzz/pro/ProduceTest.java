//package com.zzz.pro;
//
//
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.Properties;
//import java.util.Scanner;
//
//public class ProduceTest {
//    private static Scanner scanner;
//
//    private Producer<String, String> producer;
//    private Properties props;
//
//    @Before
//    public void init() {
//        props = new Properties();
//        props.put("bootstrap.servers", "159.75.98.190:9092");
//        props.put("acks", "all");
//        props.put("retries", 0);
//        props.put("batch.size", 16384);
//        props.put("linger.ms", 1);
//        props.put("buffer.memory", 33554432);
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//    }
//
//    @Test
//    public void produce() throws InterruptedException {
//        System.out.println("begin produce");
//        connectionKafka();
//        System.out.println("finish produce");
//    }
//
//    public void connectionKafka() throws InterruptedException {
//        producer = new KafkaProducer<>(props);
//        scanner = new Scanner(System.in);
//        int i = 1;
//        while (true) {
//            System.out.println("请输入要发送的消息：");
//            //String value = scanner.nextLine();
//            String value = "message";
//
//            producer.send(new ProducerRecord<String, String>("my-topic", value+i));
//            i++;
//            Thread.sleep(3000);
//        }
//
//
//    }
//
//    @After
//    public void destroy() {
//        producer.close();
//    }
//
//}