package com.chengkun.kafkaproducer.service;
/**
 * sungrow all right reserved
 **/

import com.chengkun.kafkaproducer.source.MySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;


/**
 * @Description kafka消息发送服务
 * @Author chengkun
 * @Date 2020/3/30 15:51
 **/
//这个注解给我们绑定消息通道的，Source是Stream给我们提供的，可以点进去看源码，可以看到output和input,这和配置文件中的output，input对应的。
@EnableBinding({MySource.class, Source.class})
public class SendService {

    @Autowired
    private MySource source;

    @Autowired
    private Source defaultSource;

    public void defaultSendMsg(String msg) {
        defaultSource.output().send(MessageBuilder.withPayload(msg).build());
    }

    public void sendMsg(String msg) {
        source.myOutput().send(MessageBuilder.withPayload(msg).build());
    }

    public void sendMsg1(String msg) {
        Message<String> message = MessageBuilder.withPayload(msg).setHeader("partitionKey", 0).build();
        source.Output1().send(message);
    }

    public void sendMsg2(String msg) {
        Message<String> message = MessageBuilder.withPayload(msg).setHeader("partitionKey", 1).build();
        source.Output2().send(message);
    }
}