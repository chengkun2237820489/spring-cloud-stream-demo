package rabbitconsumer2.Sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * sungrow all right reserved
 **/
public interface MySink {

    String input_1 = "input_1";

    String input_2 = "input_2";

    String delay_input = "delay_input"; //死信输入通道

    @Input(input_1)
    SubscribableChannel Input1();

    @Input(input_2)
    SubscribableChannel Input2();

    @Input(delay_input)
    SubscribableChannel delayInput();
}
