package etf.nwt.product.service;

import etf.nwt.product.config.RabbitMQConfig;
import etf.nwt.product.dto.rabbit.OrderApprovedEvent;
import etf.nwt.product.dto.rabbit.OutOfStockEvent;
import etf.nwt.product.dto.rabbit.StockReservedEvent;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.ChairVariant;
import etf.nwt.product.repository.ChairVariantRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ProductMessageListener {

    @Autowired
    private ChairVariantRepository chairVariantRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "product_stock_queue")
    @Transactional
    public void handleOrderApprovedEvent(OrderApprovedEvent event) {
        System.out.println("Received order approval request for Order ID: " + event.getOrderId());

        try {
            for (Map.Entry<Long, Integer> entry : event.getItems().entrySet()) {
                Long variantId = entry.getKey();
                Integer quantityNeeded = entry.getValue();

                if (quantityNeeded < 0) {
                    throw new InvalidOperationException("Stock quantity cannot be negative");
                }

                ChairVariant variant = chairVariantRepository.findById(variantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Chair variant not found!"));

                if (variant.getStockQuantity() < quantityNeeded) {
                    throw new InvalidOperationException("Not enough stock for chair variant: " + variant.getId());
                }

                variant.setStockQuantity(variant.getStockQuantity() - quantityNeeded);
                chairVariantRepository.save(variant);
            }

            System.out.println("Stock reserved successfully. Sending reply...");
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.STOCK_RESERVED_ROUTING_KEY,
                    new StockReservedEvent(event.getOrderId())
            );

        } catch (Exception e) {
            System.err.println("Stock reservation failed: " + e.getMessage());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.STOCK_FAILED_ROUTING_KEY,
                    new OutOfStockEvent(event.getOrderId(), e.getMessage())
            );
        }
    }
}