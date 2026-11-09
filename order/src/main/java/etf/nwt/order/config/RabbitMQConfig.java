package etf.nwt.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "saga_exchange";

    public static final String PRODUCT_STOCK_QUEUE = "product_stock_queue";

    public static final String ORDER_APPROVED_ROUTING_KEY = "order.approved";

    public static final String ORDER_REPLY_QUEUE = "order_reply_queue";
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue productStockQueue() {
        return new Queue(PRODUCT_STOCK_QUEUE, true);
    }

    @Bean
    public Binding binding(Queue productStockQueue, TopicExchange exchange) {
        return BindingBuilder.bind(productStockQueue).to(exchange).with(ORDER_APPROVED_ROUTING_KEY);
    }
    @Bean
    public Queue orderReplyQueue() {
        return new Queue(ORDER_REPLY_QUEUE, true);
    }
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}