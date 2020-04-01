package kafkaconsumer1.service;
/**
 * sungrow all right reserved
 **/

import kafkaconsumer1.Sink.MySink;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 * @Description kafka消息消费服务
 * @Author chengkun
 * @Date 2020/3/30 16:22
 **/
//消息接受端，stream给我们提供了Sink,Sink源码里面是绑定input的，要跟我们配置文件的input关联的。
@EnableBinding({MySink.class})
public class ReceiveService {

    @StreamListener(MySink.my_input)
    public void receive(Object payload) {
        System.out.println(payload);
    }

    @StreamListener(MySink.input_1)
    public void receive1(Object payload) {
        System.out.println(payload);
    }

    @StreamListener(MySink.input_2)
    public void receive2(Object payload) {
        System.out.println(payload);
    }
}