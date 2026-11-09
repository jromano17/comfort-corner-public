package etf.nwt.order.service;

import etf.nwt.order.dto.order.OrderDTO;
import etf.nwt.order.dto.orderItem.OrderItemDTO;
import etf.nwt.order.dto.orderItem.StockCheckResponseDTO;
import etf.nwt.order.exception.InvalidOperationException;
import etf.nwt.order.exception.ResourceNotFoundException;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderItem;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.repository.OrderItemRepository;
import etf.nwt.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper mapper;
    private final String PRODUCT_API_URL = "http://product/api/variants/";
    private final RestTemplate restTemplate;

    public OrderItemService(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ModelMapper mapper,
                            RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    public List<OrderItemDTO> getItemsByOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        return order.getItems()
                .stream()
                .map(orderItem -> mapper.map(orderItem, OrderItemDTO.class))
                .collect(Collectors.toList());
    }


    @Transactional
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallback")
    public OrderItemDTO addItemToOrder(Long orderId, OrderItemDTO itemDTO) {
       /* Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        if (order.getCurrentStatus() != OrderStatus.PENDING)
            throw new InvalidOperationException("Can not add items to an order which is already recieved or processing");

        OrderItem item = mapper.map(itemDTO, OrderItem.class);
        item.setOrder(order);

        order.getItems().add(item);

        order.setTotalPrice(
                order.getItems().stream()
                        .mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity())
                        .sum()
        );

        OrderItem savedItem = orderItemRepository.save(item);

        return mapper.map(savedItem, OrderItemDTO.class);
        */

        Order newOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        if (newOrder.getCurrentStatus() != OrderStatus.PENDING) {
            throw new InvalidOperationException("Can not add items to an order which is already recieved or processing");
        }

        String url = PRODUCT_API_URL + itemDTO.getVariantId() + "/check-stock";
        StockCheckResponseDTO stockInfo = restTemplate.getForObject(url, StockCheckResponseDTO.class);

        if (stockInfo == null) {
            throw new ResourceNotFoundException("Chair with ID " + itemDTO.getVariantId() + " not found");
        }
        if (!stockInfo.isInStock()) {
            throw new InvalidOperationException("Item is out of stock!");
        }

        if (stockInfo.getAvailableQuantity() < itemDTO.getQuantity()) {
            throw new InvalidOperationException("Not enough items in stock. Only " + stockInfo.getAvailableQuantity() + " left.");
        }

        /*OrderItem orderItem = new OrderItem();
        orderItem.setVariantId(itemDTO.getVariantId());
        orderItem.setQuantity(itemDTO.getQuantity());
        orderItem.setPriceAtPurchase(stockInfo.getPrice());
        orderItem.setOrder(newOrder);*/
        OrderItem orderItem = mapper.map(itemDTO, OrderItem.class);
        orderItem.setOrder(newOrder);
        orderItem.setPriceAtPurchase(stockInfo.getPrice());

        newOrder.getItems().add(orderItem);
        newOrder.setTotalPrice(newOrder.getTotalPrice() + orderItem.getQuantity() * orderItem.getPriceAtPurchase());

        OrderItem savedItem = orderItemRepository.save(orderItem);
        Order savedOrder = orderRepository.save(newOrder);
        return mapper.map(savedItem, OrderItemDTO.class);
    }


    public void removeItem(Long orderId, Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item with ID " + itemId + " not found"));

        if (!item.getOrder().getId().equals(orderId)) {
            throw new InvalidOperationException("Item does not belong to this order");
        }

        orderItemRepository.delete(item);
    }
    public Order inventoryFallback(OrderDTO request, Exception e) {
        // return null;
        throw new InvalidOperationException("Sorry! Our inventory system is currently undergoing maintenance. Please try again in 5 minutes.");
    }

    public List<OrderItemDTO> getItemsByOrderSecurely(Long orderId, Long userId, String role){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        if (!role.contains("ROLE_ADMIN") && (!role.contains("ROLE_USER") || !order.getUserId().equals(userId))) {
            throw new AccessDeniedException("You do not have permission to view this order.");
        }

        return order.getItems()
            .stream()
            .map(orderItem -> mapper.map(orderItem, OrderItemDTO.class))
            .collect(Collectors.toList());
    }
    @Transactional
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallback")
    public OrderItemDTO addItemToOrderSecurely(Long orderId, OrderItemDTO itemDTO, Long userId, String role) {
         Order newOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        if (!role.contains("ROLE_ADMIN") && (!role.contains("ROLE_USER") || !newOrder.getUserId().equals(userId))) {
            throw new AccessDeniedException("You do not have permission to view this order.");
        }

        if (newOrder.getCurrentStatus() != OrderStatus.PENDING) {
            throw new InvalidOperationException("Can not add items to an order which is already recieved or processing");
        }

        String url = PRODUCT_API_URL + itemDTO.getVariantId() + "/check-stock";
        StockCheckResponseDTO stockInfo = restTemplate.getForObject(url, StockCheckResponseDTO.class);

        if (stockInfo == null) {
            throw new ResourceNotFoundException("Chair with ID " + itemDTO.getVariantId() + " not found");
        }
        if (!stockInfo.isInStock()) {
            throw new InvalidOperationException("Item is out of stock!");
        }

        if (stockInfo.getAvailableQuantity() < itemDTO.getQuantity()) {
            throw new InvalidOperationException("Not enough items in stock. Only " + stockInfo.getAvailableQuantity() + " left.");
        }

        OrderItem orderItem = mapper.map(itemDTO, OrderItem.class);
        orderItem.setOrder(newOrder);
        orderItem.setPriceAtPurchase(stockInfo.getPrice());

        newOrder.getItems().add(orderItem);
        newOrder.setTotalPrice(newOrder.getTotalPrice() + orderItem.getQuantity() * orderItem.getPriceAtPurchase());

        OrderItem savedItem = orderItemRepository.save(orderItem);
        Order savedOrder = orderRepository.save(newOrder);
        return mapper.map(savedItem, OrderItemDTO.class);
    }

    public void removeItemSecurely(Long orderId, Long itemId, Long userId, String role) {
        Order newOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found"));

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item with ID " + itemId + " not found"));

        if (!role.contains("ROLE_ADMIN") && (!role.contains("ROLE_USER") || !newOrder.getUserId().equals(userId))) {
            throw new AccessDeniedException("You do not have permission to view this order.");
        }

        if (!item.getOrder().getId().equals(orderId)) {
            throw new InvalidOperationException("Item does not belong to this order");
        }

        orderItemRepository.delete(item);
    }
}
