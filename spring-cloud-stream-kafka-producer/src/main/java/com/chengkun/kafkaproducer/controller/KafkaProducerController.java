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

    @RequestMapping("/defaultSend/{msg}")
    public void defaultSend(@PathVariable("msg") String msg) {
        sendService.defaultSendMsg(msg);
    }

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