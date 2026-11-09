package etf.nwt.order.service;

import etf.nwt.order.dto.order.OrderDTO;
import etf.nwt.order.dto.orderItem.OrderItemDTO;
import etf.nwt.order.dto.orderItem.StockCheckResponseDTO;
import etf.nwt.order.exception.ResourceNotFoundException;
import etf.nwt.order.models.Address;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "spring.cloud.config.fail-fast=false"
})
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper mapper;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    private OrderDTO sampleOrderDTO;
    private StockCheckResponseDTO[] stockAvailable;
    private StockCheckResponseDTO[] stockUnavailable;

    @BeforeEach
    void setUp() {
        sampleOrderDTO = new OrderDTO();
        sampleOrderDTO.setUserId(1L);
        sampleOrderDTO.setOrderNumber("27177171");
        sampleOrderDTO.setCurrentStatus(OrderStatus.PENDING);
        sampleOrderDTO.setTotalPrice(100.0);
        sampleOrderDTO.setOrderNumber("171717i1717t");

        Address billing = new Address();
        billing.setFullName("John Doe");
        billing.setStreet("123 Main St");
        billing.setCity("City");
        billing.setPostalCode("10000");
        billing.setCountry("Country");
        sampleOrderDTO.setBillingAddress(billing);
        sampleOrderDTO.setShippingAddress(billing);

        sampleOrderDTO.setItems(new ArrayList<>());

        stockAvailable = new StockCheckResponseDTO[]{
                new StockCheckResponseDTO(101L, true, 10, 100.0)
        };

        stockUnavailable = new StockCheckResponseDTO[]{
                new StockCheckResponseDTO(101L, false, 0, 0.0)
        };
        mockStockAvailable();
    }
    private void mockStockAvailable() {
        Mockito.when(restTemplate.postForObject(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.eq(StockCheckResponseDTO[].class))
        ).thenReturn(stockAvailable);
    }
/*
    @Test
    void createOrder_shouldSaveOrder() {
        OrderDTO created = orderService.createOrder(sampleOrderDTO);
        assertNotNull(created.getId());
        assertEquals(OrderStatus.PENDING, created.getCurrentStatus());
    }
*/

    @Test
    void getOrder_shouldReturnOrder() {
        OrderDTO created = orderService.createOrder(sampleOrderDTO);
        OrderDTO fetched = orderService.getOrder(created.getId());
        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    void updateOrder_shouldModifyOrder() {
        //OrderDTO created = orderService.createOrder(sampleOrderDTO);
        //created.setTotalPrice(200.0);
        //OrderDTO updated = orderService.updateOrder(created.getId(), created);
        //assertEquals(200.0, updated.getTotalPrice());

        OrderDTO created = orderService.createOrder(sampleOrderDTO);
        created.setTotalPrice(200.0);
        created.setCurrentStatus(OrderStatus.PENDING);
        OrderDTO updated = orderService.updateOrder(created.getId(), created);

        assertNotNull(updated);
        assertEquals(created.getId(), updated.getId());
        assertEquals(200.0, updated.getTotalPrice());
        assertEquals(OrderStatus.PENDING, updated.getCurrentStatus());

        verify(restTemplate, atLeastOnce()).postForObject(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.eq(StockCheckResponseDTO[].class)
        );
    }

    @Test
    void updateOrder_withInvalidId_shouldThrowException() {
        OrderDTO updateDTO = new OrderDTO();
        updateDTO.setId(999L);
        updateDTO.setTotalPrice(200.0);

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.updateOrder(999L, updateDTO));
    }

    @Test
    void deleteOrder_shouldRemoveOrder() {
        //OrderDTO created = orderService.createOrder(sampleOrderDTO);
        //orderService.deleteOrder(created.getId());
        //assertThrows(ResourceNotFoundException.class, () -> orderService.getOrder(created.getId()));

        OrderDTO created = orderService.createOrder(sampleOrderDTO);
        Long orderId = created.getId();
        orderService.deleteOrder(orderId);
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrder(orderId));
        assertFalse(orderRepository.existsById(orderId));
    }
    @Test
    void deleteOrder_withNonExistentId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.deleteOrder(999L));
    }
    @Test
    void getAllOrders_shouldReturnList() {
        //orderService.createOrder(sampleOrderDTO);
        //List<OrderDTO> orders = orderService.getAllOrders();
        //assertFalse(orders.isEmpty());

        int numberBeforeAddittions = (int) orderRepository.count();
        orderService.createOrder(sampleOrderDTO);
        orderService.createOrder(sampleOrderDTO);

        List<OrderDTO> orders = orderService.getAllOrders();
        assertNotNull(orders);
        assertEquals(2 + numberBeforeAddittions, orders.size());
    }


    @Test
    void createOrder_withMultipleItems_allAvailable_shouldSucceed() {
        OrderDTO multiItemOrder = new OrderDTO();
        multiItemOrder.setUserId(1L);
        multiItemOrder.setItems(List.of(
                new OrderItemDTO(null, 101L, 2, 50.0,"ch"),
                new OrderItemDTO(null, 102L, 1, 150.0,"ch2")
        ));

        StockCheckResponseDTO[] multiStockResponse = new StockCheckResponseDTO[]{
                new StockCheckResponseDTO(101L, true, 10, 50.0),
                new StockCheckResponseDTO(102L, true, 5, 150.0)
        };

        Mockito.when(restTemplate.postForObject(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.eq(StockCheckResponseDTO[].class))
        ).thenReturn(multiStockResponse);

        // Act
        OrderDTO created = orderService.createOrder(multiItemOrder);

        // Assert
        assertNotNull(created);
        assertNotNull(created.getId());
    }
    /*
    @Test
    void testCreateOrder_WhenStockIsAvailable() {
        OrderDTO request = new OrderDTO();
        request.setItems(List.of(new OrderItemDTO(1L,101L,1, 10.0))); // Order item 101, qty 1

        // Mock the Product Service response (The "fake" synchronous answer)
        StockCheckResponseDTO[] mockResponse = {
                new StockCheckResponseDTO(101L, true, 10, 50.0) // ID 101, In Stock, Qty 10, Price $50
        };

        // TELL MOCKITO: When someone calls the URL, return my mockResponse
        Mockito.when(restTemplate.postForObject(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.eq(StockCheckResponseDTO[].class))
        ).thenReturn(mockResponse);

        // Mock the Repository save
        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT (Run the actual method)
        OrderDTO result = orderService.createOrder(request);

        // 3. ASSERT (Verify it worked)
        assertNotNull(result);
        assertEquals(50.0, result.getTotalPrice());
        assertEquals(OrderStatus.PENDING, result.getCurrentStatus());
    }*/

    @Test
    void testCreateOrder_WhenProductServiceFails() {
        // 1. ARRANGE
        OrderDTO request = new OrderDTO();
        request.setItems(List.of(new OrderItemDTO(1L,101L,1, 10.0,"chair"))); // Order item 101, qty 1

        // TELL MOCKITO: Throw an error (simulate Product Service being down)
        Mockito.when(restTemplate.postForObject(
                Mockito.anyString(), Mockito.any(), Mockito.eq(StockCheckResponseDTO[].class))
        ).thenThrow(new RuntimeException("Connection Refused"));

        // 2. ACT & ASSERT
        // We expect the Order Service to throw an exception too
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(request);
        });
    }

    /*
    @Autowired
    private EntityManager entityManager;
    private Statistics statistics;
    @Test
    public void testFetchOrders() {

        SessionFactory sessionFactory = entityManager
                .getEntityManagerFactory()
                .unwrap(SessionFactory.class);

        statistics = sessionFactory.getStatistics();
        statistics.clear();

        Order order1 = createOrderWithItems(2);
        Order order2 = createOrderWithItems(3);

        orderRepository.saveAll(List.of(order1, order2));
        entityManager.flush();
        entityManager.clear();

        statistics.clear();

        List<Order> orders = orderRepository.findAll();

        orders.forEach(o -> o.getItems().size());

        long queryCount = statistics.getQueryExecutionCount();
        System.out.println("Executed queries: " + queryCount);

        assertTrue(queryCount <= 2, "N+1 problem detected!");
    }
    private Order createOrderWithItems(int itemCount) {
        Order order = new Order();
        order.setUserId(1L);
        order.setCurrentStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(0.0);

        for (int i = 0; i < itemCount; i++) {
            OrderItem item = new OrderItem();
            item.setVariantId(100L + i);
            item.setQuantity(2);
            item.setPriceAtPurchase(50.0);
            item.setOrder(order);

            order.getItems().add(item);
            order.setTotalPrice(order.getTotalPrice() + 100);
        }

        return order;
    }*/


}
