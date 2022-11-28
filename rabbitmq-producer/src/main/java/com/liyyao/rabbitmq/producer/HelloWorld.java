package com.liyyao.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发送消息
 */
public class HelloWorld {

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
        //5、创建队列Queue
        /**
         * queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         * 参数：
         *  1、queue：队列名称
         *  2、durable：是否持久化，当mq重启之后，还在
         *  3、exclusive：是否独占。只能有一个消费者监听这个队列；当Connection关闭时，是否删除队列
         *  4、autoDelete：是否自动删除。当没有Consumer时，自动删除掉
         *  5、arguments：参数。
         */
        //如果没有一个名字叫hello_world的队列时，则会创建该队列，如果有则不会创建
        channel.queueDeclare("hello_world", true, false, false, null);
        //6、发送消息
        /**
         * basicPublish(String exchange, String routingKey, AMQP.BasicProperties props, byte[] body)
         * 参数：
         *  1、exchange：交换机名称。简单模式下交换机会使用默认的”“
         *  2、routingKey：路由名称
         *  3、props：配置信息
         *  4、body：发送消息数据
         */
        String body = "hello rabbitmq...";
        channel.basicPublish("", "hello_world", null, body.getBytes());
        //7、释放资源
        channel.close();
        connection.close();
    }
}
