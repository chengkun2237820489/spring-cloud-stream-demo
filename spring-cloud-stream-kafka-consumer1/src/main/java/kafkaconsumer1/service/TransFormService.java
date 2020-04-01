package kafkaconsumer1.service;
/**
 * sungrow all right reserved
 **/

import kafkaconsumer1.Processor.MyProcessor;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * @Description 消息中转类
 * @Author chengkun
 * @Date 2020/3/31 13:56
 **/
//@EnableBinding({MyProcessor.class})
public class TransFormService {

    @ServiceActivator(inputChannel = MyProcessor.trans_input, outputChannel = MyProcessor.trans_output)
    public Object MyTransform(Object payload) {
        System.out.println("消息中转站：" + payload);
        return payload;
    }

}