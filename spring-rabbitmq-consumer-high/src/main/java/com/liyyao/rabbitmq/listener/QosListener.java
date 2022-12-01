package com.liyyao.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * Consumer限流机制
 *  1、确保ack机制为手动确认
 *  2、listener-container配置属性
 *      perfetch = 1，表示消费端每次从mq拉取一条消息来消费，直到手动确认消费完毕后，都会继续拉取下一条消息
 */
@Component
public class QosListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //1、获取消息
        System.out.println(new String(message.getBody()));
        //2、处理业务逻辑
        Thread.sleep(2000);
        //3、签收
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
}
