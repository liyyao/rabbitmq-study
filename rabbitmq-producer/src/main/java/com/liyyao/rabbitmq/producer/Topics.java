package com.liyyao.rabbitmq.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Topics {

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
        //5、创建交换机
        /**
         * exchangeDeclare(String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments)
         * 参数：
         *  1、exchange：交换机名称
         *  2、type：交换机类型：
         *      DIRECT("direct"),：定向
         *      FANOUT("fanout"),：扇形（广播）
         *      TOPIC("topic"),：通配符的形式
         *      HEADERS("headers");：参数匹配
         *  3、durable：是否持久化
         *  4、autoDelete：自动删除
         *  5、internal：内部使用。一般false
         *  6、arguments：参数
         */
        String exchangeName = "test_topic";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, false, null);
        //6、创建队列Queue
        String queue1Name = "test_topic_queue1";
        String queue2Name = "test_topic_queue2";
        channel.queueDeclare(queue1Name, true, false, false, null);
        channel.queueDeclare(queue2Name, true, false, false, null);
        //7、绑定队列和交换机
        /**
         * queueBind(String queue, String exchange, String routingKey)
         *  参数：
         *      1、queue：队列名称
         *      2、exchange：交换机名称
         *      3、routingKey：路由键，绑定规则
         *          如果交换机的类型为fanout，routingkey设置为""
         */
        //routing key： 系统的名称.日志的级别
        //需求：所有error级别的日志存入数据库，所有order系统的日志存入数据库
        channel.queueBind(queue1Name, exchangeName, "#.error");
        channel.queueBind(queue1Name, exchangeName, "order.*");
        channel.queueBind(queue2Name, exchangeName, "*.*");
        //8、发送消息
        String body = "日志信息：张三调用了findAll方法...出错了...日志级别：info...";
        channel.basicPublish(exchangeName, "goods.error", null, body.getBytes());
        //9、释放资源
        channel.close();
        connection.close();
    }
}
