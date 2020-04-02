package com.chengkun.rabbitproducer.controller;
/**
 * sungrow all right reserved
 **/

import com.chengkun.rabbitproducer.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description rabbit消息发送控制器
 * @Author chengkun
 * @Date 2020/4/1 19:00
 **/
@RestController
public class RabbitProducerController {

    @Autowired
    SendService sendService;

    @RequestMapping("/defaultSend/{msg}")
    public void defaultSendMessage(@PathVariable("msg") String msg) {
        sendService.defaultSendMsg(msg);
    }

    @RequestMapping("/send/{msg}")
    public void SendMessage(@PathVariable("msg") String msg) {
        sendService.sendMsg(msg);
    }

    @RequestMapping("/send1/{msg}")
    public void SendMessage1(@PathVariable("msg") String msg) {
        sendService.sendMsg1(msg);
    }

    @RequestMapping("/send2/{msg}")
    public void SendMessage2(@PathVariable("msg") String msg) {
        sendService.sendMsg2(msg);
    }
}