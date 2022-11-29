package com.liyyao.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:rabbitmq.properties")
@Configuration
public class RabbitmqConfig {

    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.port}")
    private int port;
    @Value("${rabbitmq.virtual-host}")
    private String virtualHost;
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;

    @Bean
    public com.rabbitmq.client.ConnectionFactory getConnectionFactory() {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setVirtualHost(virtualHost);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory(com.rabbitmq.client.ConnectionFactory rabbitFactory) {
        return new CachingConnectionFactory(rabbitFactory);
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        return new RabbitTemplate(factory);
    }

    /**
     * 简单模式
     * @return
     */
    @Bean
    public Queue helloWorldQueue() {
        return new Queue("spring_helloworld_queue");
    }

    //***************Publish/Subscribe模式****************
    @Bean
    public Queue fanoutQueue1() {
        return new Queue("spring_fanout_queue_1");
    }

    @Bean
    public Queue fanoutQueue2() {
        return new Queue("spring_fanout_queue_2");
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("spring_fanout_exchange");
    }

    @Bean
    public Binding bindingQueue1(Queue fanoutQueue1, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    @Bean
    public Binding bindingQueue2(Queue fanoutQueue2, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }


    //***************Topic模式****************
    @Bean
    public Queue topicQueue1() {
        return new Queue("spring_topic_queue_star");
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue("spring_topic_queue_well");
    }

    @Bean
    public Queue topicQueue3() {
        return new Queue("spring_topic_queue_well2");
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("spring_topic_exchange");
    }

    @Bean
    public Binding topicBindingQueue1(Queue topicQueue1, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueue1).to(topicExchange).with("liyyao.*");
    }

    @Bean
    public Binding topicBindingQueue2(Queue topicQueue2, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueue2).to(topicExchange).with("liyyao.#");
    }

    @Bean
    public Binding topicBindingQueue3(Queue topicQueue3, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueue3).to(topicExchange).with("liyy.#");
    }
}
