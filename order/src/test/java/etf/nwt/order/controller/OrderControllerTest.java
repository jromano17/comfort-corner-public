package etf.nwt.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import etf.nwt.order.dto.order.OrderDTO;
import etf.nwt.order.dto.orderItem.OrderItemDTO;
import etf.nwt.order.dto.orderStatusHistory.OrderStatusHistoryDTO;
import etf.nwt.order.models.Address;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.service.OrderItemService;
import etf.nwt.order.service.OrderService;
import etf.nwt.order.service.OrderStatusHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
// org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ActiveProfiles("test")
@WebMvcTest(OrderController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.fail-fast=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderItemService orderItemService;

    @MockBean
    private OrderStatusHistoryService historyService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private OrderDTO sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = new OrderDTO();
        sampleOrder.setId(1L);
        sampleOrder.setUserId(1L);
        sampleOrder.setOrderNumber("171717i1717t");
        sampleOrder.setCurrentStatus(OrderStatus.PENDING);
        sampleOrder.setTotalPrice(100.0);
        sampleOrder.setCreatedAt(LocalDateTime.now());
        sampleOrder.setBillingAddress(new Address("Jalal","Crkvice","Zenica","72000","BiH"));
        sampleOrder.setShippingAddress(new Address("Jalal","Crkvice","Zenica","72000","BiH"));

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

   /* @Test
    void getAllOrders_shouldReturnList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(sampleOrder));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }*/

    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(sampleOrder);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createOrder_invalidData_shouldReturnBadRequest() throws Exception {
        OrderDTO invalidOrder = new OrderDTO();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void updateOrder_shouldReturnUpdatedOrder() throws Exception {
        sampleOrder.setTotalPrice(200.0);
        when(orderService.updateOrder(eq(1L), any(OrderDTO.class))).thenReturn(sampleOrder);

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(200.0));
    }

    @Test
    void deleteOrder_shouldReturnOk() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk());
    }


    @Test
    void shouldGetOrderItems() throws Exception {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setId(10L);
        when(orderItemService.getItemsByOrder(1L)).thenReturn(List.of(itemDTO));

        mockMvc.perform(get("/api/orders/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    void shouldAddOrderItem() throws Exception {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setId(10L);
        itemDTO.setPriceAtPurchase(17.11);
        itemDTO.setQuantity(2);
        itemDTO.setVariantId(1L);
        when(orderItemService.addItemToOrder(eq(1L), any(OrderItemDTO.class))).thenReturn(itemDTO);

        mockMvc.perform(post("/api/orders/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void shouldRemoveOrderItem() throws Exception {
        mockMvc.perform(delete("/api/orders/1/items/10"))
                .andExpect(status().isOk());

        verify(orderItemService).removeItem(1L, 10L);
    }


    @Test
    void shouldUpdateOrderStatus() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setId(1L);
        when(historyService.recordStatusChange(1L, OrderStatus.PENDING, 123L)).thenReturn(dto);

        mockMvc.perform(patch("/api/orders/1/status")
                        .param("newStatus", "PENDING")
                        .param("changedBy", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldGetStatusHistory() throws Exception {
        OrderStatusHistoryDTO historyDTO = new OrderStatusHistoryDTO();
        historyDTO.setId(100L);
        when(historyService.getHistoryByOrderId(1L)).thenReturn(List.of(historyDTO));

        mockMvc.perform(get("/api/orders/1/status-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L));
    }
    @Test
    void getAllOrders_WithDefaultPagination_ReturnsPage() throws Exception {
        Page<OrderDTO> mockPage = createMockOrderPage(0, 20, 100);
        when(orderService.getAllOrders(0, 20, "createdAt,desc"))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(100))
                .andExpect(jsonPath("$.totalPages").value(5));

        verify(orderService).getAllOrders(0, 20, "createdAt,desc");
    }

    @Test
    void getAllOrders_WithCustomPagination_ReturnsCorrectPage() throws Exception {
        Page<OrderDTO> mockPage = createMockOrderPage(2, 5, 50);
        when(orderService.getAllOrders(2, 5, "totalPrice,desc"))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/orders")
                        .param("page", "2")
                        .param("size", "5")
                        .param("sort", "totalPrice,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable.pageNumber").value(2))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));

        verify(orderService).getAllOrders(2, 5, "totalPrice,desc");
    }

    @Test
    void getAllOrders_WithInvalidPage_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("page", "-1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrdersByStatus_ReturnsFilteredPage() throws Exception {
        Page<OrderDTO> mockPage = createMockOrderPage(0, 10, 25);
        when(orderService.getOrdersByStatus(OrderStatus.PROCESSING, 0, 10))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/orders/status/PROCESSING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].currentStatus").value("PROCESSING"));

        verify(orderService).getOrdersByStatus(OrderStatus.PROCESSING, 0, 10);
    }

    @Test
    void getOrdersByStatus_WithInvalidStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/orders/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserOrders_ReturnsUserOrdersPage() throws Exception {
        Long userId = 123L;
        Page<OrderDTO> mockPage = createMockOrderPage(0, 5, 15);

        mockPage.getContent().forEach(order -> order.setUserId(userId));

        when(orderService.getOrdersByUser(userId, 0, 5))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/orders/user/{userId}", userId)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value(userId))
                .andExpect(jsonPath("$.content.length()").value(10));

        verify(orderService).getOrdersByUser(userId, 0, 5);
    }

    @Test
    void getUserOrders_WithNoOrders_ReturnsEmptyPage() throws Exception {
        Long userId = 999L;
        Page<OrderDTO> emptyPage = Page.empty();

        when(orderService.getOrdersByUser(userId, 0, 10))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/orders/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(orderService).getOrdersByUser(userId, 0, 10);
    }

    private Page<OrderDTO> createMockOrderPage(int pageNumber, int pageSize, int totalElements) {
        List<OrderDTO> orders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            OrderDTO order = new OrderDTO();
            order.setId((long) i + 1);
            order.setUserId((long) (i % 3) + 100);
            order.setCurrentStatus(OrderStatus.PROCESSING);
            order.setTotalPrice(100.0 + i * 10);
            order.setCreatedAt(LocalDateTime.now().minusDays(i));
            order.setId((long) i + 1);
            orders.add(order);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new PageImpl<>(orders, pageable, totalElements);
    }

}
