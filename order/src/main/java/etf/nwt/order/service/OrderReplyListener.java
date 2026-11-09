package etf.nwt.order.service;

import etf.nwt.order.config.RabbitMQConfig;
import etf.nwt.order.dto.rabbit.OutOfStockEvent;
import etf.nwt.order.dto.rabbit.StockReservedEvent;
import etf.nwt.order.exception.ResourceNotFoundException;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.models.OrderStatusHistory;
import etf.nwt.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderReplyListener {

    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQConfig.ORDER_REPLY_QUEUE)
    @Transactional
    public void handleStockReserved(StockReservedEvent event) {
        System.out.println("Received StockReservedEvent for Order ID: " + event.getOrderId());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));

        order.setCurrentStatus(OrderStatus.CREATED);

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(OrderStatus.CREATED);
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);

        orderRepository.save(order);

        System.out.println("Order " + order.getId() + " is now finalized and CREATED.");
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_REPLY_QUEUE)
    @Transactional
    public void handleOutOfStock(OutOfStockEvent event) {
        System.err.println("Received OutOfStockEvent for Order ID: " + event.getOrderId() + ". Reason: " + event.getReason());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found!"));

        order.setCurrentStatus(OrderStatus.CANCELLED);

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(OrderStatus.CANCELLED);
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistory().add(history);

        orderRepository.save(order);

        System.err.println("Compensating transaction executed. Order " + order.getId() + " is CANCELED.");
    }
}