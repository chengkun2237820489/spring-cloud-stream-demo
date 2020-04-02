package com.chengkun.rabbitproducer.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * sungrow all right reserved
 **/
public interface MySource {

    String myOutput = "myOutput";   //管道名称为"myOutput"

    String output_1 = "output_1";

    String output_2 = "output_2";

    @Output(myOutput)
    MessageChannel myOutput();

    @Output(output_1)
    MessageChannel Output1();

    @Output(output_2)
    MessageChannel Output2();
}
