//package com.zzz.pro.config;
//
//import org.apache.kafka.clients.producer.Partitioner;
//import org.apache.kafka.common.Cluster;
//import org.apache.kafka.common.InvalidRecordException;
//import org.apache.kafka.common.PartitionInfo;
//import org.apache.kafka.common.utils.Utils;
//
//import java.util.List;
//import java.util.Map;
//
//
////自定义kafka分区器
//public class KafkaPartition implements Partitioner {
//
//
//
//    @Override
//    public int partition(String topic, Object key, byte[] keyBytes, Object o1, byte[] bytes1, Cluster cluster) {
//        //从clusten中获取这个topicn分区信息，在此我门建立了一个partition为5的topic
//        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
//        //获取分区总个数
//        int numPartitions = partitions.size();
//        if(key == null){
//            throw new InvalidRecordException("We expect all messages to have customer name as key...");
//        }else{
//            //假设这里我们让key为hangzhou的记录放在一个专门的partition中
//            if(((String)key).equals("hangzhou")){
//                /*
//                 * 返回partition，partition是int类型的，值就是一个在numPartitions范围内的一个值，范围从0开始，
//                 * 这里我们定义返回partition为最后一个分区（也就是序号最大的partition），这样才可以保证在else的情况中我们的partition分配的范围是在除去专属于"hangzhou"的partition中，
//                 * 因为在else中减1之后，Partition不会达到这个原本最大的partition序号，也就不会再分到这个partition
//                 * 原本的partition序号是0~4，如果我们指定的是4，在else中取余的时候减1，partition的范围就变成了0~3，肯定不会分到4
//                 * 但如果我们在这里指定的不是4（那就是0~3），而在else中的范围是0~3，就肯定会有记录分配到专属partition中
//                 * 否则，假如在这里指定的是partition为3的partition，那在else的情况中虽然partition个数减1了，
//                 * 但是并没有体现是排除了这里专属的partition，就导致其他的key的记录在hash算法时还会分到这里
//                 */
//                return numPartitions - 1;
//            }else{
//                //其余的记录按照散列算法的方式分布在其他partition中，此时partition的个数是在总个数上减1，而且是上面的专属partition已经排除了
//                return (Math.abs(Utils.murmur2(keyBytes)) % (numPartitions - 1));
//            }
//        }
//    }
//
//    @Override
//    public void close() {
//
//    }
//
//    @Override
//    public void configure(Map<String, ?> map) {
//
//    }
//}
//
//
//
//
//
//
