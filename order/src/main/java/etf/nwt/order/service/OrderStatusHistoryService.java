package etf.nwt.order.service;

import etf.nwt.order.dto.rabbit.OrderApprovedEvent;
import etf.nwt.order.dto.order.OrderDTO;
import etf.nwt.order.dto.orderStatusHistory.OrderStatusHistoryDTO;
import etf.nwt.order.exception.ResourceNotFoundException;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderItem;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.models.OrderStatusHistory;
import etf.nwt.order.repository.OrderRepository;
import etf.nwt.order.repository.OrderStatusHistoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderStatusHistoryService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final ModelMapper mapper;
    private OrderMessagePublisher messagePublisher;

    public OrderStatusHistoryService(OrderRepository orderRepository,
                                     OrderStatusHistoryRepository historyRepository,
                                     ModelMapper mapper,
                                     OrderMessagePublisher messagePublisher) {
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
        this.mapper = mapper;
        this.messagePublisher = messagePublisher;
    }

    public OrderDTO recordStatusChange(Long orderId, OrderStatus newStatus, Long changedBy) {
     /*   Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        order.setCurrentStatus(newStatus);

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy(changedBy);

        order.getStatusHistory().add(history);

        return mapper.map(orderRepository.save(order), OrderDTO.class);*/

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        order.setCurrentStatus(newStatus);
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy(changedBy);

        order.getStatusHistory().add(history);


        OrderDTO dto = mapper.map(orderRepository.save(order), OrderDTO.class);

        if (newStatus == OrderStatus.APPROVED){
            Map<Long, Integer> itemsToReserve = order.getItems().stream()
                    .collect(Collectors.toMap(
                            OrderItem::getVariantId,
                            OrderItem::getQuantity
                    ));
            OrderApprovedEvent event = new OrderApprovedEvent(order.getId(), itemsToReserve);
            messagePublisher.sendOrderApprovedEvent(event);
        }

        return dto;
    }

    public List<OrderStatusHistoryDTO> getHistoryByOrderId(Long orderId){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return order.getStatusHistory()
                .stream()
                .map(statusHistory -> mapper.map(statusHistory, OrderStatusHistoryDTO.class))
                .collect(Collectors.toList());
    }

    public OrderStatus getOrderStatus(Long orderId, Long userId, String role){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!role.contains("ROLE_ADMIN") && (!role.contains("ROLE_USER") || !order.getUserId().equals(userId))) {
            throw new AccessDeniedException("You do not have permission to view this order.");
        }
        return order.getCurrentStatus();
    }

    public List<OrderStatusHistoryDTO> getHistoryByOrderIdSecurely(Long orderId, Long userId, String role){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!role.contains("ROLE_ADMIN") && (!role.contains("ROLE_USER") || !order.getUserId().equals(userId))) {
            throw new AccessDeniedException("You do not have permission to view this order.");
        }
        return order.getStatusHistory()
                .stream()
                .map(statusHistory -> mapper.map(statusHistory, OrderStatusHistoryDTO.class))
                .collect(Collectors.toList());
    }
}
