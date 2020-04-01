package kafkaconsumer1.Processor;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * sungrow all right reserved
 **/
public interface SendToBinder {
    @Output("trans_output")
    MessageChannel output();

    @Input("trans_input")
    SubscribableChannel input();
}
