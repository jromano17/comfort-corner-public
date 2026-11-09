package etf.nwt.order.service;

import etf.nwt.order.dto.order.OrderDTO;
import etf.nwt.order.dto.orderItem.OrderItemDTO;
import etf.nwt.order.dto.orderItem.StockCheckResponseDTO;
import etf.nwt.order.exception.InvalidOperationException;
import etf.nwt.order.exception.ResourceNotFoundException;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderItem;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.models.OrderStatusHistory;
import etf.nwt.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper mapper;

    private final String PRODUCT_BATCH_URL = "http://product/api/chair-variants/check-stock-batch";
    private final RestTemplate restTemplate;


    public OrderService(OrderRepository orderRepository, ModelMapper mapper, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> mapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
        return mapper.map(order, OrderDTO.class);
    }

    @Transactional
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallback")
    public OrderDTO createOrder(OrderDTO dto) {
        /*Order order = mapper.map(dto, Order.class);
        Order saved = orderRepository.save(order);
        return mapper.map(saved, OrderDTO.class);
        prije uvodjenja sync komunikacije*/

        List<Long> variantIds = dto.getItems().stream()
                .map(OrderItemDTO::getVariantId)
                .collect(Collectors.toList());

        StockCheckResponseDTO[] stockResponses = restTemplate.postForObject(
                PRODUCT_BATCH_URL,
                variantIds,
                StockCheckResponseDTO[].class
        );

        if (stockResponses == null || stockResponses.length == 0) {
            throw new InvalidOperationException("Can not create order without any order items");
        }
        Map<Long, StockCheckResponseDTO> stockMap = Arrays.stream(stockResponses)
                .collect(Collectors.toMap(StockCheckResponseDTO::getVariantId, Function.identity()));


        Order newOrder = new Order();
        newOrder.setCurrentStatus(OrderStatus.PENDING);
        double totalOrderPrice = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO itemRequest : dto.getItems()) {
            StockCheckResponseDTO stock = stockMap.get(itemRequest.getVariantId());

            if (stock == null) {
                throw new ResourceNotFoundException("Product with ID:" + itemRequest.getVariantId() + " does not exist.");
            }

            if (stock.getAvailableQuantity() < itemRequest.getQuantity()) {
                throw new InvalidOperationException("Product " + stock.getVariantId() + " has insufficient stock. Only " + stock.getAvailableQuantity() + " left.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setVariantId(itemRequest.getVariantId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(stock.getPrice());
            orderItem.setOrder(newOrder);
            orderItems.add(orderItem);

            totalOrderPrice += (stock.getPrice() * itemRequest.getQuantity());
        }

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setStatus(OrderStatus.PENDING);
        orderStatusHistory.setChangedAt(LocalDateTime.now());
        orderStatusHistory.setOrder(newOrder);
        orderStatusHistory.setChangedBy(dto.getUserId());

        newOrder.setStatusHistory(new ArrayList<>(Collections.singleton(orderStatusHistory)));
        newOrder.setItems(orderItems);
        newOrder.setTotalPrice(totalOrderPrice);
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUserId(dto.getUserId());
        newOrder.setBillingAddress(dto.getBillingAddress());
        newOrder.setShippingAddress(dto.getShippingAddress());
        Order saved = orderRepository.save(newOrder);
        return mapper.map(saved, OrderDTO.class);
    }

    public OrderDTO updateOrder(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));

        mapper.map(dto, order);

        Order saved = orderRepository.save(order);
        return mapper.map(saved, OrderDTO.class);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));
        orderRepository.deleteById(id);
    }

    public Page<OrderDTO> getAllOrders(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equals(sortParams[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        Page<Order> orders = orderRepository.findAll(pageable);
        System.out.println("Order service");
        System.out.println(orders.get().count());
        return orders.map(order -> mapper.map(order, OrderDTO.class));
    }

    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findByCurrentStatus(status, pageable);

        return orders.map(order -> mapper.map(order, OrderDTO.class));
    }

    public Page<OrderDTO> getOrdersByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);

        return orders.map(order -> mapper.map(order, OrderDTO.class));
    }

    public List<OrderDTO> getHighValuePendingOrders(Double minAmount, Integer daysBack) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(daysBack);

        List<Order> orders = orderRepository.findHighValuePendingOrders(minAmount, sinceDate);

        return orders.stream()
                .map(order -> mapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public Order inventoryFallback(OrderDTO request, Exception e) {
        // return null;
        throw new InvalidOperationException("Sorry! Our inventory system is currently undergoing maintenance. Please try again in 5 minutes.");
    }

    public OrderDTO getOrderSecurely(Long orderId, Long userId, String role)  {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        if (!role.contains("ROLE_ADMIN") && (!role.contains("ROLE_USER") || !order.getUserId().equals(userId))) {
            throw new AccessDeniedException("You do not have permission to view this order.");
        }
        return mapper.map(order, OrderDTO.class);
    }

    public void deleteOrderSecurely(Long orderId, Long userId, String role)  {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        if (!role.contains("ROLE_ADMIN") && (!role.contains("ROLE_USER") || !order.getUserId().equals(userId))) {
            throw new AccessDeniedException("You do not have permission to view this order.");
        }
        orderRepository.deleteById(orderId);
    }

    public OrderDTO updateOrderSecurely(Long id, OrderDTO dto, Long userId, String role) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found"));

        if (!role.contains("ROLE_ADMIN") && (!role.contains("ROLE_USER") || !order.getUserId().equals(userId))) {
            throw new AccessDeniedException("You do not have permission to view this order.");
        }
        dto.setId(null); 
        mapper.map(dto, order);
        Order saved = orderRepository.save(order);
        return mapper.map(saved, OrderDTO.class);
    }

}
