package com.liyyao.rabbitmq;

import com.liyyao.rabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = RabbitmqConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ProducerTest2 {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testHelloWorld() {
        rabbitTemplate.convertAndSend("spring_helloworld_queue", "hello world spring...");
    }

    /**
     * 发送fanout消息
     */
    @Test
    public void testFanout() {
        rabbitTemplate.convertAndSend("spring_fanout_exchange", "", "spring fanout...");
    }

    /**
     * 发送topic消息
     */
    @Test
    public void testTopics() {
        rabbitTemplate.convertAndSend("spring_topic_exchange", "liyy.hehe.hah", "spring topic2...");
    }
}
