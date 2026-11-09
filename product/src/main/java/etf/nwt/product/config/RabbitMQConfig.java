package etf.nwt.product.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "saga_exchange";
    public static final String ORDER_REPLY_QUEUE = "order_reply_queue";

    public static final String STOCK_RESERVED_ROUTING_KEY = "stock.reduced";
    public static final String STOCK_FAILED_ROUTING_KEY = "stock.failed";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue orderReplyQueue() {
        return new Queue(ORDER_REPLY_QUEUE, true);
    }

    @Bean
    public Binding successBinding(Queue orderReplyQueue, TopicExchange exchange) {
        return BindingBuilder.bind(orderReplyQueue).to(exchange).with(STOCK_RESERVED_ROUTING_KEY);
    }

    @Bean
    public Binding failedBinding(Queue orderReplyQueue, TopicExchange exchange) {
        return BindingBuilder.bind(orderReplyQueue).to(exchange).with(STOCK_FAILED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}