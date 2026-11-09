package etf.nwt.order.service;

import etf.nwt.order.config.RabbitMQConfig;
import etf.nwt.order.dto.rabbit.OrderApprovedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderMessagePublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendOrderApprovedEvent(OrderApprovedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ORDER_APPROVED_ROUTING_KEY,
                event
        );

        System.out.println("Published OrderApprovedEvent to RabbitMQ for Order ID: " + event.getOrderId());
    }
}