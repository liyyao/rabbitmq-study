package com.liyyao.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rabbitmq-producer.xml")
public class ProducerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 确认模式：
     *  1、确认模式开启：connectionFactory中开启confirm-type="CORRELATED"
     *  2、在rabbitTemplate定义ConfirmCallBack回调函数
     */
    @Test
    public void testConfirm() {
        //定义回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * @param correlationData 相关配置
             * @param ack exchange交换机是否成功收到了消息。true成功，false代表失败
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm方法被执行了...");
                if (ack) {
                    System.out.println("接收成功消息：" + cause);
                } else {
                    System.out.println("接收失败消息：" + cause);
                    //做一些处理，让消息再次发送
                }
            }
        });

        //发送消息
        rabbitTemplate.convertAndSend("spring_exchange_confirm", "confirm", "message confirm...");
    }

    /**
     * 回退模式：确当消息发送给Exchange后，Exchange路由到Queue失败时 才会执行ReturnCallBack
     *  1、开启回退模式：publisher-returns="true"
     *  2、设置ReturnCallBack
     *  3、设置Exchange处理消息的模式：
     *      1、如果消息没有路由到Queue，则丢弃消息（默认）
     *      2、如果消息没有路由到Queue，返回给消息发送方ReturnCallBack
     */
    @Test
    public void testReturn() {
        //设置交换机处理失败消息的模式
        rabbitTemplate.setMandatory(true);
        //定义回调
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            /**
             * @param returnedMessage
             * message： 消息对象
             * replyCode：错误码
             * replyText：错误信息
             * exchange：交换机
             * routingKey：路由键
             */
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                System.out.println("return 执行了...");
                System.out.println(returnedMessage.getMessage());
                System.out.println(returnedMessage.getReplyCode());
                System.out.println(returnedMessage.getReplyText());
                System.out.println(returnedMessage.getExchange());
                System.out.println(returnedMessage.getRoutingKey());

                //处理，再次路由到另一个key等等。
            }
        });

        //发送消息
        rabbitTemplate.convertAndSend("spring_exchange_confirm", "confirm", "message return...");
    }

    /**
     * TTL：过期时间
     *  1、队列的统一过期
     *      x-message-ttl
     *  2、消息单独过期
     *
     *  如果设置了消息的过期时间，也设置了队列的过期时间，它以时间短的为准。
     *  队列过期后，会将队列所有消息全部移除。
     *  消息过期后，只能消息在队列顶端，才会判断其是否过期（移除掉）
     */
    @Test
    public void testTtl() {
        /*for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hehe", "message ttl...");
        }*/
        //消息后处理对象，设置一些消息的参数信息
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //1、设置message的信息
                message.getMessageProperties().setExpiration("5000");//消息的过期时间
                //2、返回该消息
                return message;
            }
        };
        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hehe", "message ttl...", messagePostProcessor);
            } else {
                rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hehe", "message ttl...");
            }
        }
    }

    /**
     * 发送测试死信消息：
     *  1、过期时间
     *  2、长度限制
     *  3、消息拒收
     */
    @Test
    public void testDlx() {
        //1、测试过期时间，死信消息
        //rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.haha", "我是一条消息，我会成为死信么？");

        //2、测试长度限制后，消息死信
        /*for (int i = 0; i < 20; i++) {
            rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.haha", "我是一条消息，我会成为死信么？");
        }*/

        //3、测试消息拒收
        //rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.haha", "我是一条消息，我会成为死信么？");

        //4、测试单个消息过期
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //1、设置message的信息
                message.getMessageProperties().setExpiration("5000");//消息的过期时间
                //2、返回该消息
                return message;
            }
        };
        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.haha", "message dlx...", messagePostProcessor);
            } else {
                rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.haha", "message dlx...");
            }
        }
    }

    @Test
    public void testDelay() throws InterruptedException {
        //1、发送订单消息，将来是在订单系统中，下单成功后，发送消息
        rabbitTemplate.convertAndSend("order_exchange", "order.msg", "订单信息：id=1,time=202211301155");

        //2、打印倒计时20秒
        for (int i = 0; i < 20; i++) {
            System.out.println(i + "...");
            Thread.sleep(1000);
        }
    }
}
