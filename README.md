概念
1. 发布/订阅

简单的讲就是一种生产者，消费者模式。发布者是生产，将输出发布到数据中心，订阅者是消费者，订阅自己感兴趣的数据。当有数据到达数据中心时，就把数据发送给对应的订阅者。

2. 消费组
直观的理解就是一群消费者一起处理消息。需要注意的是：每个发送到消费组的数据，仅由消费组中的一个消费者处理。

3. 分区
类比于消费组，分区是将数据分区。举例：某应用有多个实例，都绑定到同一个数据中心，也就是不同实例都将数据发布到同一个数据中心。分区就是将数据中心的数据再细分成不同的区。为什么需要分区？因为即使是同一个应用，不同实例发布的数据类型可能不同，也希望这些数据由不同的消费者处理。这就需要，消费者可以仅订阅一个数据中心的部分数据。这就需要分区这个东西了。

Spring Cloud Stream简介
1. 应用模型
Spring Cloud Stream应用由第三方的中间件组成。应用间的通信通过输入通道（input channel）和输出通道（output channel）完成。这些通道是有Spring Cloud Stream 注入的。而通道与外部的代理（可以理解为上文所说的数据中心）的连接又是通过Binder实现的。

说明：最底层是消息服务，中间层是绑定层，绑定层和底层的消息服务进行绑定，顶层是消息生产者和消息消费者，顶层可以向绑定层生产消息和和获取消息消费

2. 抽象的Binder
Binder可以理解为提供了Middleware操作方法的类。Spring Cloud 提供了Binder抽象接口以及KafKa和Rabbit MQ的Binder的实现。

使用Spring Cloud Stream
1. 快速开始
这里先放出前面的应用模型图


2. 使用Spring Cloud Stream整合Kafka
下面例子使用的Middleware是Kafka，版本是kafka_2.11-1.0.0。Kafka使用的是默认配置，也就是从Kafka官网下载好后直接打开，不更改任何配置。

创建spring-cloud-stream父工程，pom如下所示：
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.chengkun</groupId>
    <artifactId>spring-cloud-stream</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-cloud-stream</name>
    <description>Demo project for Spring Boot</description>

    <modules>
        <module>spring-cloud-stream-kafka-producer</module>
        <module>spring-cloud-stream-kafka-consumer1</module>
        <module>spring-cloud-stream-kafka-consumer2</module>
    </modules>
    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Hoxton.SR3</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-test-support</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

 
 首先我们要在先前的工程中新建三个子模块，分别是spring-cloud-stream-kafka-producer，spring-cloud-stream-kafka-consumer1，spring-cloud-stream-kafka-consumer2  这三个模块，其中spring-cloud-stream-kafka-producer作为生产者进行发消息模块，spring-cloud-stream-kafka-consumer1，spring-cloud-stream-kafka-consumer2作为消息接收模块。

创建kafka消息生产者，引入以下依赖：
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.chengkun</groupId>
        <artifactId>spring-cloud-stream</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    <groupId>com.chengkun</groupId>
    <artifactId>spring-cloud-stream-kafka-producer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-cloud-stream-kafka-producer</name>
    <description>Demo project for Spring Boot</description>


    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-binder-kafka</artifactId>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>


接着进行application.yml进行配置如下：
server:
  port: 7888
spring:
  application:
    name: kafka-producer
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true        #如果设置为false,就不会自动创建Topic 有可能你Topic还没创建就直接调用了。
      bindings:
        output:      #这里用stream给我们提供的默认output，后面会讲到自定义output
          destination: kafka-demo    #消息发往的目的地
          content-type: text/plain    #消息发送的格式，接收端不用指定格式，但是发送端要


接下来进行第一个spring-cloud-stream-kafka-producer模块的代码编写，在该模块下定义一个SendService，如下：
package com.chengkun.kafkaproducer.service;
/**
 * sungrow all right reserved
 **/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @Description 消息发送服务
 * @Author chengkun
 * @Date 2020/3/30 15:51
 **/
//这个注解给我们绑定消息通道的，Source是Stream给我们提供的，可以点进去看源码，可以看到output和input,这和配置文件中的output，input对应的。
@EnableBinding(Source.class)
public class SendService {

    @Autowired
    private Source source;

    public void sendMsg(String msg){
        source.output().send(MessageBuilder.withPayload(msg).build());
    }
}


spring-cloud-stream-kafka-producer的controller层代码如下：
package com.chengkun.kafkaproducer.controller;
/**
 * sungrow all right reserved
 **/

import com.chengkun.kafkaproducer.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description kafka消息提供者
 * @Author chengkun
 * @Date 2020/3/30 15:54
 **/
@RestController
public class KafkaProducerController {

    @Autowired
    private SendService sendService;

    @RequestMapping("/send/{msg}")
    public void send(@PathVariable("msg") String msg) {
        sendService.sendMsg(msg);
    }
}


接下来进行spring-cloud-stream-kafka-consumer1，spring-cloud-stream-kafka-consumer1两个模块的代码编写，首先需要引入的依赖，上面已经提到。
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-binder-kafka</artifactId>
        </dependency>
　　　　<dependency>
   　　　　<groupId>org.springframework.boot</groupId>
   　　　　<artifactId>spring-boot-starter-web</artifactId>
　　　　</dependency>

接着进行spring-cloud-stream-kafka-consumer1和spring-cloud-stream-kafka-consumer2模块application.yml的配置，如下：

spring-cloud-stream-kafka-consumer1配置如下：
server:
  port: 7889
spring:
  application:
    name: consumer_1
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        #input是接收，注意这里不能再像前面一样写output了
        input:
          destination: kafka-demo


spring-cloud-stream-kafka-consumer2模块application.yml的配置如下：
server:
  port: 7890
spring:
  application:
    name: consumer_2
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        #input是接收，注意这里不能再像前面一样写output了
        input:
          destination: kafka-demo


好了接下来进行spring-cloud-stream-kafka-consumer1模块和spring-cloud-stream-kafka-consumer2模块的消息接受代码的编写，spring-cloud-stream-kafka-consumer1模块和spring-cloud-stream-kafka-consumer2模块的消息接受代码都是一样的，如下：
package kafkaconsumer1.service;
/**
 * sungrow all right reserved
 **/

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * @Description kafka消息消费服务
 * @Author chengkun
 * @Date 2020/3/30 16:22
 **/
//消息接受端，stream给我们提供了Sink,Sink源码里面是绑定input的，要跟我们配置文件的input关联的。
@EnableBinding(Sink.class)
public class ReceiveService {

    @StreamListener(Sink.INPUT)
    public void receive(Object payload){
        System.out.println(payload);
    }
}


	@StreamListener(管道id，如Sink.INPUT)。管道id是在接收器中定义的。
我们首先要启动zookeeper，和Kafka，如下：


接着分别现后启动启动spring-cloud-stream-kafka-producer，spring-cloud-stream-kafka-consumer1，spring-cloud-stream-kafka-consumer2,模块运行结果如下：
首先进行spring-cloud-stream-kafka-producer模块的访问，如下：
localhost:7888/send/hello


注：auto-create-topics: true      自动创建Topic未生效，并且启动消费者服务会报错，可能是使用远程kafka引起的，需要在kafka服务器手动创建topic，创建命令如下：
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafka-demo

回车后可以看到，Kafka CommitId,说明消息发送成功，再看一下，那两个消息接受模块的输出，如下：




可以看到这两消息模块都接收到了消息并且打印了出来。
      好了到现在为止，我们进行了一个简单的消息发送和接收，用的是Stream给我们提供的默认Source，Sink，接下来我们要自己进行自定义，这种方式在工作中还是用的比较多的，因为我们要往不同的消息通道发消息，必然不能全都叫input,output的，那样的话就乱套了，因此首先自定义一个接口，如下：
Source（发射器） :一个接口类，内部定义了一个输出管道，例如定义一个输出管道 @output（"XXOO"）。说明这个发射器将会向这个管道发射数据。
Sink（接收器） : 一个接口类，内部定义了一个输入管道，例如定义一个输入管道 @input（"XXOO"）。说明这个接收器将会从这个管道接收数据。
Binder（绑定器）:用于与管道进行绑定。Binder将于消息中间件进行关联。@EnableBinding （Source.class/Sink.class）。@EnableBinding（）里面是可以定义多个发射器/接收器

自定义MySource：
package com.chengkun.kafkaproducer.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * sungrow all right reserved
 **/
public interface MySource {

    String str = "myOutput";   //管道名称为"myOutput"

    @Output(str)
    MessageChannel myOutput();
}

 
这里要注意一下，可以看到上面的代码，其中myOutput是要和你的配置文件的消息发送端配置对应的，因此修改spring-cloud-stream-kafka-producer中application.yml配置，如下：
server:
  port: 7888
spring:
  application:
    name: kafka-producer
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true        #如果设置为false,就不会自动创建Topic 有可能你Topic还没创建就直接调用了。
      bindings:
#        output:      #这里用stream给我们提供的默认output，后面会讲到自定义output
        myOutput:   #自定义output
          destination: kafka-demo    #消息发往的目的地
          content-type: text/plain    #消息发送的格式，接收端不用指定格式，但是发送端要


spring.cloud.stream.bindings.myOutput.destination=XXOO 是可以定义多个的，表示myOutput与XXOO主题的映射。

这样还不行，还必须改造spring-cloud-stream-kafka-producer消息发送端的SendService这个类，代码如下：
package com.chengkun.kafkaproducer.service;
/**
 * sungrow all right reserved
 **/

import com.chengkun.kafkaproducer.source.MySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @Description kafka消息发送服务
 * @Author chengkun
 * @Date 2020/3/30 15:51
 **/
//这个注解给我们绑定消息通道的，Source是Stream给我们提供的，可以点进去看源码，可以看到output和input,这和配置文件中的output，input对应的。
@EnableBinding(MySource.class)
public class SendService {

    @Autowired
    private MySource source;

    public void sendMsg(String msg){
        source.myOutput().send(MessageBuilder.withPayload(msg).build());
    }
}


接下来重新启动那三个模块，运行结果如下：




可以看到两个消息接收端还是依然能接受消息。
然后我们定义多个topic进行生产和消费数据，修改自定义Source（发射器），如下:
package com.chengkun.kafkaproducer.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * sungrow all right reserved
 **/
public interface MySource {

    String str = "myOutput";   //管道名称为"myOutput"

    String output_1 = "output_1";

    String output_2 = "output_2";

    @Output(str)
    MessageChannel myOutput();

    @Output(output_1)
    MessageChannel Output1();

    @Output(output_2)
    MessageChannel Output2();
}


需要修改生产者的配置文件，如下：
server:
  port: 7888
spring:
  application:
    name: kafka-producer
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true        #如果设置为false,就不会自动创建Topic 有可能你Topic还没创建就直接调用了。
      bindings:
        #        output:      #这里用stream给我们提供的默认output，后面会讲到自定义output
        myOutput:   #自定义output,可定义多个
          destination: kafka-demo    #消息发往的目的地
          content-type: text/plain    #消息发送的格式，接收端不用指定格式，但是发送端要
        output_1:   #自定义output
          destination: kafka-demo1
          content-type: text/plain
        output_2:   #自定义output
          destination: kafka-demo2
          content-type: text/plain

改造消息发送service和controller，如下：
消息发送service：
package com.chengkun.kafkaproducer.service;
/**
 * sungrow all right reserved
 **/

import com.chengkun.kafkaproducer.source.MySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @Description kafka消息发送服务
 * @Author chengkun
 * @Date 2020/3/30 15:51
 **/
//这个注解给我们绑定消息通道的，Source是Stream给我们提供的，可以点进去看源码，可以看到output和input,这和配置文件中的output，input对应的。
@EnableBinding(MySource.class)
public class SendService {

    @Autowired
    private MySource source;

    public void sendMsg(String msg){
        source.myOutput().send(MessageBuilder.withPayload(msg).build());
    }

    public void sendMsg1(String msg){
        source.Output1().send(MessageBuilder.withPayload(msg).build());
    }

    public void sendMsg2(String msg){
        source.Output2().send(MessageBuilder.withPayload(msg).build());
    }
}


消息发送controller：
package com.chengkun.kafkaproducer.controller;
/**
 * sungrow all right reserved
 **/

import com.chengkun.kafkaproducer.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description kafka消息提供者
 * @Author chengkun
 * @Date 2020/3/30 15:54
 **/
@RestController
public class KafkaProducerController {

    @Autowired
    private SendService sendService;

    @RequestMapping("/send/{msg}")
    public void send(@PathVariable("msg") String msg) {
        sendService.sendMsg(msg);
    }

    @RequestMapping("/send1/{msg}")
    public void send1(@PathVariable("msg") String msg) {
        sendService.sendMsg1(msg);
    }

    @RequestMapping("/send2/{msg}")
    public void send2(@PathVariable("msg") String msg) {
        sendService.sendMsg2(msg);
    }
}

改造消息消费者与生产者类似，spring-cloud-stream-kafka-consumer1和spring-cloud-stream-kafka-consumer2一样，首先自定义Sink（接收器），进行接受消息，绑定对应通道，如下：
package kafkaconsumer1.Sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * sungrow all right reserved
 **/
public interface MySink {

    String my_input = "myInput";   //管道名称为"myOutput"

    String input_1 = "input_1";

    String input_2 = "input_2";

    @Input(my_input)
    SubscribableChannel myInput();

    @Input(input_1)
    SubscribableChannel Input1();

    @Input(input_2)
    SubscribableChannel Input2();
}


修改消费者配置文件，如下：
server:
  port: 7889
spring:
  application:
    name: consumer_1
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        #input是接收，注意这里不能再像前面一样写output了
#       input:
        myInput: #自定义input
          destination: kafka-demo
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2

修改消息消费服务service，如下：
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
@EnableBinding(MySink.class)
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

注：这里的Binder（绑定器）可以绑定多个Source（发射器）和Sink（接收器），根据不同业务进行定义，如下所示：
@EnableBinding({MySource.class, Source.class})
@EnableBinding({MySink.class, Sink.class})


spring-cloud-stream还给我们提供了一个Processor接口，用于进行消息处理后再进行发送出去，相当于一个消息中转站。下面我们进行演示
　　首先我们需要改造spring-cloud-stream-kafka-consumer1模块，把它作为一个消息中转站。用于spring-cloud-stream-kafka-consumer1消息处理后再进行发送给spring-cloud-stream-kafka-consumer2模块
首先修改spring-cloud-stream-kafka-consumer1模块的配置，如下：
server:
  port: 7889
spring:
  application:
    name: consumer_1
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        #input是接收，注意这里不能再像前面一样写output了
        input:
          destination: test
        myInput: #自定义input
          destination: kafka-demo
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2
        #进行消息中转处理后，在进行转发出去           
        output:
          destination: kafka-demo-trans


接着在新建一个消息中转类，代码如下：
package kafkaconsumer1.service;
/**
 * sungrow all right reserved
 **/

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * @Description 消息中转类
 * @Author chengkun
 * @Date 2020/3/31 13:56
 **/
@EnableBinding(Processor.class)
public class TransFormService {

    @ServiceActivator(inputChannel = Processor.INPUT,outputChannel = Processor.OUTPUT)
    public Object transform(Object payload){
        System.out.println("消息中转站："+payload);
        return payload;
    }

}


接着要修改消息中转站发送消息出去的接收端spring-cloud-stream-kafka-consumer2的配置，如下：
server:
  port: 7890
spring:
  application:
    name: consumer_2
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        #input是接收，这里接收消息中转类的消息
        input:
          destination: kafka-demo-trans
        myInput: #自定义input
          destination: kafka-demo
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2


修改接收消息服务，添加接收消息中转消息：
@StreamListener(Sink.INPUT)
public void TransFormReceive(Object payload) {
    System.out.println("接收消息中转消息" + payload);
}


这里需要创建中转topic，如下：
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafka-demo-trans

这里要强调一下，要把先前spring-cloud-stream-kafka-consumer1中RecieveService类的绑定注解全都注释掉，不然，会绑定冲突的，同时如果消息被其他通道消费也不能进行中转，接下来分别重启这三个模块，运行结果如下：

中转站运行结果取下：


接下来，看中转后的的接受端spring-cloud-stream-kafka-consumer2的消息，到底有没有消息过来，如下:

可以看到，中转后消息被接受到了。

注：这里中转类消息不能被其他通道消费，不然无法中转，同时不能绑定中转通道进行消费，否则无法中转，我们查看Processor类可以发现他继承Source, Sink接口，所以中转使用的是默认通道，所以使用原生的Processor类不能绑定Source, Sink接口，否则会消费中转的消息

接下来我们进行自定义Processor类进行单独处理消息中转，新建中转接口MyProcessor，如下：
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


修改中转类，如下：
package kafkaconsumer1.service;
/**
 * sungrow all right reserved
 **/

import kafkaconsumer1.Processor.MyProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * @Description 消息中转类
 * @Author chengkun
 * @Date 2020/3/31 13:56
 **/
@EnableBinding({MyProcessor.class})
public class TransFormService {

    @ServiceActivator(inputChannel = MyProcessor.trans_input, outputChannel = MyProcessor.trans_output)
    public Object MyTransform(Object payload) {
        System.out.println("消息中转站：" + payload);
        return payload;
    }

}


修改spring-cloud-stream-kafka-consumer1配置文件，添加中转消息管道，如下：
server:
  port: 7889
spring:
  application:
    name: consumer_1
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        myInput: #自定义input
          destination: kafka-demo
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2
        #trans_input 进行消息中转处理后，中转输入管道
        trans_input:
          destination: test
        #trans_output 进行消息中转处理后，中转输出管道
        trans_output:
          destination: kafka-demo-trans


修改spring-cloud-stream-kafka-consumer2配置文件，添加中转消费消息管道，如下：
server:
  port: 7890
spring:
  application:
    name: consumer_2
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        #input是接收，这里接收消息中转类的消息
        input:
          destination: test
        myInput: #自定义input
          destination: kafka-demo
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2
        trans_input:
          destination: kafka-demo-trans


注：通过测试，发现管道与topic进行绑定不能有多个管道消费topic，管道与topic是一一对应，绑定的管道与定义名称一致，不是变量名，是变量名定义的名称，这里是生产者发送消息到test，消费者1订阅消息test进行转发到kafka-demo-trans，消费者2订阅kafka-demo-trans进行输出

我们还可能会遇到一个场景就是，我们接收到消息后，给别人一个反馈ACK，SpringCloud stream 给我们提供了一个SendTo注解可以帮我们干这些事情。
首先我们在spring-cloud-stream-kafka-consumer2先实现一个接口SendToBinder去实现output和input,代码如下：
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


接着再新建一个SendToService类来绑定自己的SendToBinder接口，然后监听input,返回ACK表示中转站收到消息了，再转发消息出去，代码如下：
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


这里要注意一点就是，启动前下那边之前的用到的哪些绑定注解，先注释掉，不然与这里会发生冲突，如下：

运行结果如下：






可以看到发送端受到一个ACK
 
可以看到先前的例子，我们都是一端发消息，两个消息接受者都接收到了，但是有时候有些业务场景我只想让其中一个消息接收者接收到消息，那么该怎么办呢？
这时候就涉及一个消息分组(Consumer Groups)的概念了。

消息分组(Consumer Groups)
“Group”，如果使用过 Kafka 的读者并不会陌生。Spring Cloud Stream 的这个分组概念的意思基本和 Kafka 一致。微服务中动态的缩放同一个应用的数量以此来达到更高的处理能力是非常必须的。对于这种情况，同一个事件防止被重复消费，
　　只要把这些应用放置于同一个 “group” 中，就能够保证消息只会被其中一个应用消费一次。不同的组是可以消费的，同一个组内会发生竞争关系，只有其中一个可以消费。
首先修改该spring-cloud-stream-kafka-consumer1模块的配置，修改代码如下：
server:
  port: 7889
spring:
  application:
    name: consumer_1
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        myInput: #自定义input
          destination: kafka-demo
          #分组的组名
          group: group
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2
        #trans_input 进行消息中转处理后，中转输入管道
        trans_input:
          destination: test
        #trans_output 进行消息中转处理后，中转输出管道
        trans_output:
          destination: kafka-demo-trans


接着修改spring-cloud-stream-kafka-consumer2模块的配置，代码如下：
server:
  port: 7890
spring:
  application:
    name: consumer_2
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        #input是接收，这里接收消息中转类的消息
        input:
          destination: test
        myInput: #自定义input
          destination: kafka-demo
          #分组的组名
          group: group
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2
        trans_input:
          destination: kafka-demo-trans

可以看到spring-cloud-stream-kafka-consumer1和spring-cloud-stream-kafka-consumer2的kafka-demo是属于同一组的。spring-cloud-stream-kafka-producer模块的发的消息只能被spring-cloud-stream-kafka-consumer1或spring-cloud-stream-kafka-consumer2其中一个接收到，这样避免了重复消费。

注：这里重复消费分组指的是同一个topic不能重复消费不是管道名称，例如：consumer_1和consumer_2的trans_input管道相同topic不同为同一组时不影响程序运行，都会被消费，发送多条消息，同一group下是随机消费的，但是只能被一个group消费

有时候我们只想给特定的消费者消费消息，那么又该真么做呢？
这是后又涉及到消息分区的概念了。

消息分区()
Spring Cloud Stream对给定应用的多个实例之间分隔数据予以支持。在分隔方案中，物理交流媒介（如：代理主题）被视为分隔成了多个片（partitions）。一个或者多个生产者应用实例给多个消费者应用实例发送消息并确保相同特征的数据被同一消费者实例处理。 
Spring Cloud Stream对分割的进程实例实现进行了抽象。使得Spring Cloud Stream 为不具备分区功能的消息中间件（RabbitMQ）也增加了分区功能扩展。
那么我们就要进行一些配置了，比如我只想要spring-cloud-stream-kafka-consumer2模块接收到消息，
spring-cloud-stream-kafka-consumer1配置如下：
server:
  port: 7889
spring:
  application:
    name: consumer_1
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        myInput: #自定义input
          destination: kafka-demo
          #分组的组名
          group: group
          consumer:
            #开启分区
            partitioned: true
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2
        #trans_input 进行消息中转处理后，中转输入管道
        trans_input:
          destination: test
        #trans_output 进行消息中转处理后，中转输出管道
        trans_output:
          destination: kafka-demo-trans
      #分区数量
      instance-count: 2
      #设置当前实例的索引号，从 0 开始
      instance-index: 0


spring-cloud-stream-kafka-consumer2配置如下：
server:
  port: 7890
spring:
  application:
    name: consumer_2
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        #input是接收，这里接收消息中转类的消息
        input:
          destination: test
        myInput: #自定义input
          destination: kafka-demo
          #分组的组名
          group: group
          consumer:
            #开启分区
            partitioned: true
        input_1: #自定义input
          destination: kafka-demo1
        input_2: #自定义input
          destination: kafka-demo2
        trans_input:
          destination: kafka-demo-trans
      #分区数量
      instance-count: 2
      instance-index: 1

生产者端spring-cloud-stream-kafka-producer模块配置如下：
server:
  port: 7888
spring:
  application:
    name: kafka-producer
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true        #如果设置为false,就不会自动创建Topic 有可能你Topic还没创建就直接调用了。
          auto-add-partitions: true
      bindings:
        output:  #这里用stream给我们提供的默认output，后面会讲到自定义output
          destination: test    #消息发往的目的地
          content-type: text/plain    #消息发送的格式，接收端不用指定格式，但是发送端要
        myOutput:   #自定义output,可定义多个
          destination: kafka-demo    #消息发往的目的地
          content-type: text/plain    #消息发送的格式，接收端不用指定格式，但是发送端要
          producer:
            #分区的主键，根据什么来分区，下面的payload只是发送消息的id，例如：hello1的id为1，如果是对象直接是id属性
            partitionKeyExpression: payload
            #Key和分区数量进行取模去分配消息，这里分区数量配置为2
            partitionCount: 2
        output_1:   #自定义output
          destination: kafka-demo1
          content-type: text/plain
        output_2:   #自定义output
          destination: kafka-demo2
          content-type: text/plain


页面访问发送消息如下：
http://localhost:7888/send/hello1
http://localhost:7888/send/hello2

控制台输出如下：



这种分区发送方式局限性较大，不适应业务，我们还可以根据消息头来进行分析如下所示：
server:
  port: 7888
spring:
  application:
    name: kafka-producer
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true        #如果设置为false,就不会自动创建Topic 有可能你Topic还没创建就直接调用了。
          auto-add-partitions: true
      bindings:
        output:  #这里用stream给我们提供的默认output，后面会讲到自定义output
          destination: test    #消息发往的目的地
          content-type: text/plain    #消息发送的格式，接收端不用指定格式，但是发送端要
        myOutput:   #自定义output,可定义多个
          destination: kafka-demo    #消息发往的目的地
          content-type: text/plain    #消息发送的格式，接收端不用指定格式，但是发送端要
          producer:
            #分区的主键，根据什么来分区，下面的payload只是发送消息的id，例如：hello1的id为1，如果是对象直接是id属性
            partition-key-expression: payload
            #Key和分区数量进行取模去分配消息，这里分区数量配置为2
            partitionCount: 2
        output_1:   #自定义output
          destination: kafka-demo1
          content-type: text/plain
          producer:
            partition-key-expression: headers['partitionKey']     #payload.id#这个是分区表达式, 例如当表达式的值为1, 那么在订阅者的instance-index中为1的接收方, 将会执行该消息.
            partition-count: 2                                    #指定参与消息分区的消费端节点数量为2个
        output_2:   #自定义output
          destination: kafka-demo2
          content-type: text/plain
          producer:
            partition-key-expression: headers['partitionKey']     #payload.id#这个是分区表达式, 例如当表达式的值为1, 那么在订阅者的instance-index中为1的接收方, 将会执行该消息.
            partition-count: 2                                    #指定参与消息分区的消费端节点数量为2个


spring-cloud-stream-kafka-consumer1配置如下：
server:
  port: 7889
spring:
  application:
    name: consumer_1
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        myInput: #自定义input
          destination: kafka-demo
          group: group  #分组的组名
          consumer:
            partitioned: true  #开启分区
        input_1: #自定义input
          destination: kafka-demo1
          group: group1  #分组的组名
          consumer:
            partitioned: true  #开启分区
        input_2: #自定义input
          destination: kafka-demo2
          group: group2 #分组的组名
          consumer:
            partitioned: true  #开启分区
        trans_input:    #trans_input 进行消息中转处理后，中转输入管道
          destination: test
        trans_output:    #trans_output 进行消息中转处理后，中转输出管道
          destination: kafka-demo-trans
      #分区数量
      instance-count: 2
      #设置当前实例的索引号，从 0 开始
      instance-index: 0

spring-cloud-stream-kafka-consumer2配置如下：
server:
  port: 7890
spring:
  application:
    name: consumer_2
  cloud:
    stream:
      kafka:
        binder:
          brokers: 192.168.200.242:9092         #Kafka的消息中间件服务器
          zk-nodes: 192.168.200.242:2181        #Zookeeper的节点，如果集群，后面加,号分隔
          auto-create-topics: true
      bindings:
        input:    #input是接收，这里接收消息中转类的消息
          destination: test
        myInput: #自定义input
          destination: kafka-demo
          group: group  #分组的组名
          consumer:     #开启分区
            partitioned: true
        input_1: #自定义input
          destination: kafka-demo1
          group: group1  #分组的组名
          consumer:
            partitioned: true  #开启分区
        input_2: #自定义input
          destination: kafka-demo2
          group: group2 #分组的组名
          consumer:
            partitioned: true  #开启分区
        trans_input:
          destination: kafka-demo-trans
      #分区数量
      instance-count: 2
      instance-index: 1

修改kafka-demo1和kafka-demo2消息发送，添加header，如下：
package com.chengkun.kafkaproducer.service;
/**
 * sungrow all right reserved
 **/

import com.chengkun.kafkaproducer.source.MySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;


/**
 * @Description kafka消息发送服务
 * @Author chengkun
 * @Date 2020/3/30 15:51
 **/
//这个注解给我们绑定消息通道的，Source是Stream给我们提供的，可以点进去看源码，可以看到output和input,这和配置文件中的output，input对应的。
@EnableBinding({MySource.class, Source.class})
public class SendService {

    @Autowired
    private MySource source;

    @Autowired
    private Source defaultSource;

    public void defaultSendMsg(String msg) {
        defaultSource.output().send(MessageBuilder.withPayload(msg).build());
    }

    public void sendMsg(String msg) {
        source.myOutput().send(MessageBuilder.withPayload(msg).build());
    }

    public void sendMsg1(String msg) {
        Message<String> message = MessageBuilder.withPayload(msg).setHeader("partitionKey", 0).build();
        source.Output1().send(message);
    }

    public void sendMsg2(String msg) {
        Message<String> message = MessageBuilder.withPayload(msg).setHeader("partitionKey", 1).build();
        source.Output2().send(message);
    }
}


通过页面访问http://localhost:7888/send1/hello被spring-cloud-stream-kafka-consumer1消费，http://localhost:7888/send2/hello被spring-cloud-stream-kafka-consumer2消费

注：spring-cloud-stream-kafka-consumer1接收消息的header为0，分区索引instance-index: 0，spring-cloud-stream-kafka-consumer1接收消息header为1，分区索引instance-index: 1
