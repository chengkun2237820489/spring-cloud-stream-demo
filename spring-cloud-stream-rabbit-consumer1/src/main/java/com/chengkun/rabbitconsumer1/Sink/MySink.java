package com.chengkun.rabbitconsumer1.Sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * sungrow all right reserved
 **/
public interface MySink {

    String my_input = "myInput";   //管道名称为"myInput"

    String input_1 = "input_1";

    String input_2 = "input_2";

    @Input(my_input)
    SubscribableChannel myInput();

    @Input(input_1)
    SubscribableChannel Input1();

    @Input(input_2)
    SubscribableChannel Input2();
}
