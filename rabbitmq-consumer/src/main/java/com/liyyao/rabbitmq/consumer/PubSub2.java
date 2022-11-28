package com.liyyao.rabbitmq.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PubSub2 {

    public static void main(String[] args) throws IOException, TimeoutException {
        //1、创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //2、设置参数
        factory.setHost("43.142.250.57");
        factory.setPort(5672);
        factory.setVirtualHost("liyyao");
        factory.setUsername("liyyao");
        factory.setPassword("admin");
        //3、创建连接Connection
        Connection connection = factory.newConnection();
        //4、创建Channel
        Channel channel = connection.createChannel();

        String queue1Name = "test_fanout_queue1";
        String queue2Name = "test_fanout_queue2";
        //6、接收消息
        /**
         * basicConsume(String queue, boolean autoAck, Consumer callback)
         * 参数：
         *  1、queue；队列名称
         *  2、autoAck：是否自动确认
         *  3、callback：回调对象
         */
        Consumer consumer = new DefaultConsumer(channel) {
            /**
             * 回调方法，当收到消息后，会自动执行该方法
             * @param consumerTag 标识
             * @param envelope  获取一些信息，交换机，路由key...
             * @param properties 配置信息
             * @param body 数据
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                /*System.out.println("consumerTag: " + consumerTag);
                System.out.println("properties: " + envelope.getExchange());
                System.out.println("RoutingKey: " + properties);*/
                System.out.println("body: " + new String(body));
                System.out.println("将日志信息保存数据库...");
            }
        };
        channel.basicConsume(queue2Name, true, consumer);

        //7、关闭资源？不要
    }
}
