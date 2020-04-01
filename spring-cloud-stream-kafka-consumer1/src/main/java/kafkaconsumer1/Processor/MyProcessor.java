package kafkaconsumer1.Processor;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * sungrow all right reserved
 **/
public interface MyProcessor {

    String trans_input = "trans_input";   //中转输入管道

    String trans_output = "trans_output";   //中转输出管道


    @Input(trans_input)
    SubscribableChannel transInput();

    @Output(trans_output)
    MessageChannel transOutput();
}
