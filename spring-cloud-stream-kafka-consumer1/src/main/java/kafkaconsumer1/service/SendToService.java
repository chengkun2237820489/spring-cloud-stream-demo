package kafkaconsumer1.service;
/**
 * sungrow all right reserved
 **/

import kafkaconsumer1.Processor.SendToBinder;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

/**
 * @Description 接收消息，反馈ACK 
 * @Author chengkun
 * @Date 2020/3/31 19:15
 **/
@EnableBinding(SendToBinder.class)
public class SendToService {

    @StreamListener("trans_input")
    @SendTo("trans_output")
    public Object receiveFromInput(Object payload){
        System.out.println("中转消息。。"+payload);
        return "xxxxx";
    }

}