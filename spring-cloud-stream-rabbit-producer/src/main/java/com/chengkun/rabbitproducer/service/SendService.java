package com.chengkun.rabbitproducer.service;
/**
 * sungrow all right reserved
 **/

import com.chengkun.rabbitproducer.source.MySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @Description rabbit消息发送
 * @Author chengkun
 * @Date 2020/4/1 18:46
 **/
@EnableBinding({Source.class, MySource.class})
public class SendService {

    @Autowired
    private Source defaultSource;

    @Autowired
    private MySource source;

    public void defaultSendMsg(String msg) {
        defaultSource.output().send(MessageBuilder.withPayload(msg).build());
    }

    public void sendMsg(String msg) {
        source.myOutput().send(MessageBuilder.withPayload(msg).build());
    }

    public void sendMsg1(String msg) {
        //指定RoutingKey为1
        Message<String> message = MessageBuilder.withPayload(msg).setHeader("routingKey",1).build();
        source.Output1().send(message);
    }

    public void sendMsg2(String msg) {
        //指定RoutingKey为2
        Message<String> message = MessageBuilder.withPayload(msg).setHeader("routingKey",2).build();
        source.Output2().send(message);
    }
}