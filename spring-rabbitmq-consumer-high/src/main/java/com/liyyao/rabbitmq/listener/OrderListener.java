package com.liyyao.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;


/**
 * Consumer ACK机制：
 *  1、设置手动签收： acknowledge="manual"
 *  2、让监听器类实现ChannelAwareMessageListener接口
 *  3、如果消息成功处理，则调用channel的basicAck()签收
 *  4、如果消息处理失败，则调用channel的basicNack()拒绝签收，broker重新发送给consumer
 */
@Component
public class OrderListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //1、接收转换消息
            System.out.println(new String(message.getBody()));
            //2、处理业务逻辑
            System.out.println("处理业务逻辑...");
            System.out.println("根据订单id查询其状态...");
            System.out.println("判断状态是否为支付成功...");
            System.out.println("取消订单，回滚库存");
            System.out.println();
            //3、手动签收
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            System.out.println("出现异常，拒绝接收消息....");
            //4、拒绝签收
            /**
             * 第三个参数：requeue，重回队列。如果设置为true，则消息重新回到queue，broker会重新发送该消息给消费端
             */
            channel.basicNack(deliveryTag, true, true);
        }
    }
}
