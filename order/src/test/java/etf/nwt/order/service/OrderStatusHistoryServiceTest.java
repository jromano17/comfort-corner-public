package etf.nwt.order.service;

import etf.nwt.order.dto.order.OrderDTO;
import etf.nwt.order.dto.orderStatusHistory.OrderStatusHistoryDTO;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.models.OrderStatusHistory;
import etf.nwt.order.repository.OrderRepository;
import etf.nwt.order.repository.OrderStatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderStatusHistoryServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusHistoryRepository historyRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private OrderStatusHistoryService historyService;

    @Test
    void shouldRecordStatusChange() {
        Order order = new Order();
        order.setId(1L);
        order.setStatusHistory(new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderDTO dto = new OrderDTO();
        when(mapper.map(order, OrderDTO.class)).thenReturn(dto);

        OrderDTO result = historyService.recordStatusChange(1L, OrderStatus.SHIPPED, 123L);

        assertNotNull(result);
        assertEquals(1, order.getStatusHistory().size());
        assertEquals(OrderStatus.SHIPPED, order.getCurrentStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldGetHistoryByOrderId() {
        Order order = new Order();
        order.setId(1L);

        OrderStatusHistory history = new OrderStatusHistory();
        history.setId(10L);

        order.setStatusHistory(List.of(history));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(mapper.map(history, OrderStatusHistoryDTO.class))
                .thenReturn(new OrderStatusHistoryDTO());

        List<OrderStatusHistoryDTO> result = historyService.getHistoryByOrderId(1L);

        assertEquals(1, result.size());
        verify(orderRepository).findById(1L);
    }
}
