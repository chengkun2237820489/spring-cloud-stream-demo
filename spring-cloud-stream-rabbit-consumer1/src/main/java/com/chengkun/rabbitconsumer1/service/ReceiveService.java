package com.chengkun.rabbitconsumer1.service;
/**
 * sungrow all right reserved
 **/

import com.chengkun.rabbitconsumer1.Sink.MySink;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

import java.text.SimpleDateFormat;

/**
 * @Description rabbit消费服务
 * @Author chengkun
 * @Date 2020/4/1 20:06
 **/
@EnableBinding({Sink.class, MySink.class})
public class ReceiveService {

    @StreamListener(Sink.INPUT)
    public void defaultReceive(Message<String> message) {
        System.out.println(message.getPayload());
    }

    @StreamListener(MySink.my_input)
    public void receive(Message<String> message) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(System.currentTimeMillis()) + " " + "一般监听收到开始：" + message.getPayload());
        try {
            Thread.sleep(5000); //延迟消费时间5秒，测试死信消息
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(df.format(System.currentTimeMillis()) + " " + "一般监听收到结束：" + message.getPayload());
    }

    @StreamListener(MySink.input_1)
    public void receive1(Message<String> message) {
        System.out.println(message.getPayload());
    }

    @StreamListener(MySink.input_2)
    public void receive2(Message<String> message) {
        System.out.println(message.getPayload());
    }
}