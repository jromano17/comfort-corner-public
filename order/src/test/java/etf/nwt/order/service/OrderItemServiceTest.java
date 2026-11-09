package etf.nwt.order.service;

import etf.nwt.order.dto.orderItem.OrderItemDTO;
import etf.nwt.order.dto.orderItem.StockCheckResponseDTO;
import etf.nwt.order.exception.InvalidOperationException;
import etf.nwt.order.exception.ResourceNotFoundException;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderItem;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.repository.OrderItemRepository;
import etf.nwt.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private RestTemplate restTemplate;
    private StockCheckResponseDTO stockAvailable = new StockCheckResponseDTO(101L, true, 10, 100.0);

    @Test
    void shouldGetItemsByOrder() {
        Order order = new Order();
        order.setId(1L);

        OrderItem item = new OrderItem();
        item.setId(10L);
        item.setQuantity(2);
        item.setPriceAtPurchase(50.0);

        order.setItems(List.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(mapper.map(item, OrderItemDTO.class)).thenReturn(new OrderItemDTO());

        List<OrderItemDTO> result = orderItemService.getItemsByOrder(1L);

        assertEquals(1, result.size());
        verify(orderRepository).findById(1L);
    }
    @Test
    void shouldAddItemToOrder() {

        Order order = new Order();
        order.setId(1L);
        order.setItems(new ArrayList<>());
        order.setCurrentStatus(OrderStatus.PENDING);
        order.setTotalPrice(0.0);

        Long chairVariantId = 101L;
        Integer quantity = 3;
        Double pricePerItem = 20.0;
        Double expectedTotal = quantity * pricePerItem;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setVariantId(chairVariantId);
        dto.setQuantity(quantity);
        dto.setPriceAtPurchase(pricePerItem);

        OrderItem item = new OrderItem();
        item.setVariantId(chairVariantId);
        item.setQuantity(quantity);
        item.setPriceAtPurchase(pricePerItem);

        StockCheckResponseDTO stockResponse = new StockCheckResponseDTO(
                chairVariantId, true, 10, pricePerItem
        );
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(StockCheckResponseDTO.class))
        ).thenReturn(stockResponse);

        when(mapper.map(dto, OrderItem.class)).thenReturn(item);
        when(orderItemRepository.save(item)).thenReturn(item);
        when(mapper.map(item, OrderItemDTO.class)).thenReturn(dto);

        OrderItemDTO saved = orderItemService.addItemToOrder(1L, dto);

        assertNotNull(saved);
        assertEquals(quantity, saved.getQuantity());
        assertEquals(pricePerItem, saved.getPriceAtPurchase());
        assertEquals(expectedTotal, order.getTotalPrice());

        verify(orderRepository).findById(1L);
        verify(orderItemRepository).save(item);
        verify(orderRepository).save(order);
    }
    @Test
    void shouldThrowExceptionWhenChairNotFound() {
        Order order = new Order();
        order.setId(1L);
        order.setItems(new ArrayList<>());
        order.setCurrentStatus(OrderStatus.PENDING);

        Long nonExistentChairId = 999L;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setVariantId(nonExistentChairId);
        dto.setQuantity(3);
        dto.setPriceAtPurchase(20.0);

        StockCheckResponseDTO stockResponse = new StockCheckResponseDTO(
                nonExistentChairId, false, 0, 0.0
        );
        StockCheckResponseDTO[] stockResponses = {stockResponse};

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ResourceNotFoundException.class,
                () -> orderItemService.addItemToOrder(1L, dto));

        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenStockInsufficient() {
        Order order = new Order();
        order.setId(1L);
        order.setItems(new ArrayList<>());
        order.setCurrentStatus(OrderStatus.PENDING);

        Long chairVariantId = 101L;
        Integer requestedQuantity = 15;
        Integer availableStock = 10;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setVariantId(chairVariantId);
        dto.setQuantity(requestedQuantity);
        dto.setPriceAtPurchase(20.0);

        StockCheckResponseDTO stockResponse = new StockCheckResponseDTO(
                chairVariantId, true, availableStock, 20.0
        );
        StockCheckResponseDTO[] stockResponses = {stockResponse};

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ResourceNotFoundException.class,
                () -> orderItemService.addItemToOrder(1L, dto));

        verify(orderItemRepository, never()).save(any());
    }

    /*@Test
    void shouldAddItemToOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setItems(new ArrayList<>());
        order.setCurrentStatus(OrderStatus.PENDING);

        OrderItemDTO dto = new OrderItemDTO();
        dto.setQuantity(3);
        dto.setPriceAtPurchase(20.0);

        OrderItem item = new OrderItem();
        item.setQuantity(3);
        item.setPriceAtPurchase(20.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(mapper.map(dto, OrderItem.class)).thenReturn(item);
        when(orderItemRepository.save(item)).thenReturn(item);
        when(mapper.map(item, OrderItemDTO.class)).thenReturn(dto);

        OrderItemDTO saved = orderItemService.addItemToOrder(1L, dto);

        assertNotNull(saved);
        assertEquals(60.0, order.getTotalPrice()); // 3*20
        verify(orderItemRepository).save(item);
    }*/

    @Test
    void shouldRemoveItemFromOrder() {
        Order order = new Order();
        order.setId(1L);

        OrderItem item = new OrderItem();
        item.setId(10L);
        item.setOrder(order);

        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(item));

        orderItemService.removeItem(1L, 10L);

        verify(orderItemRepository).delete(item);
    }

    @Test
    void shouldThrowWhenRemovingItemNotBelongingToOrder() {
        Order otherOrder = new Order();
        otherOrder.setId(2L);

        OrderItem item = new OrderItem();
        item.setId(10L);
        item.setOrder(otherOrder);

        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(item));

        InvalidOperationException ex = assertThrows(InvalidOperationException.class,
                () -> orderItemService.removeItem(1L, 10L));

        assertEquals("Item does not belong to this order", ex.getMessage());
    }
}
