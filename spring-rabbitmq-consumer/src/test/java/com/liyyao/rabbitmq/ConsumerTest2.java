package com.liyyao.rabbitmq;

import com.liyyao.rabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RabbitmqConfig.class)
public class ConsumerTest2 {

    @Test
    public void testHelloWorld() {
        boolean flag = true;
        while (flag) {

        }
    }
}
