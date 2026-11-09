package etf.nwt.order.service;

import etf.nwt.order.dto.incomeRecord.IncomeRecordDTO;
import etf.nwt.order.models.IncomeRecord;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderItem;
import etf.nwt.order.repository.IncomeRecordRepository;
import etf.nwt.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeRecordServiceTest {

    @Mock
    private IncomeRecordRepository incomeRecordRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private IncomeRecordService incomeService;

    @Test
    void shouldGetAllIncomeRecords() {
        IncomeRecord record = new IncomeRecord();
        record.setId(1L);

        when(incomeRecordRepository.findAll()).thenReturn(List.of(record));
        when(mapper.map(record, IncomeRecordDTO.class))
                .thenReturn(new IncomeRecordDTO());

        List<IncomeRecordDTO> result = incomeService.getAllIncomeRecords();

        assertEquals(1, result.size());
        verify(incomeRecordRepository).findAll();
    }

    @Test
    void shouldGetIncomeRecordById() {
        IncomeRecord record = new IncomeRecord();
        record.setId(1L);

        when(incomeRecordRepository.findById(1L))
                .thenReturn(Optional.of(record));
        when(mapper.map(record, IncomeRecordDTO.class))
                .thenReturn(new IncomeRecordDTO());

        IncomeRecordDTO dto = incomeService.getIncomeRecord(1L);

        assertNotNull(dto);
    }

    @Test
    void shouldRecordIncome() {
        Order order = new Order();
        order.setId(10L);
        order.setTotalPrice(100.0);
        OrderItem item = new OrderItem();
        item.setVariantId(1L);
        item.setQuantity(11);
        item.setPriceAtPurchase(17.11);
        item.setOrder(order);
        item.setId(1L);
        order.setItems(new ArrayList<>(Collections.singleton(item)));

        when(orderRepository.findById(10L))
                .thenReturn(Optional.of(order));
        when(incomeRecordRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.map(any(IncomeRecord.class), eq(IncomeRecordDTO.class)))
                .thenReturn(new IncomeRecordDTO());

        IncomeRecordDTO dto = incomeService.recordIncome(10L);

        assertNotNull(dto);
        verify(incomeRecordRepository).save(any());
    }
/*
    @Test
    void shouldUpdateIncomeRecord() {
        IncomeRecord record = new IncomeRecord();
        record.setId(1L);

        IncomeRecordDTO dto = new IncomeRecordDTO();
        dto.setIncomeAmount(150.0);
        dto.setOrderId(10L);
        dto.setCost(700.);
        dto.setRecordedAt(LocalDateTime.now());
        dto.setNetIncome(1700.);
        dto.setTax(17.);
        dto.setId(1L);


        when(incomeRecordRepository.findById(1L))
                .thenReturn(Optional.of(record));

        when(incomeRecordRepository.save(record))
                .thenReturn(record);
        when(mapper.map(record, IncomeRecordDTO.class))
                .thenReturn(dto);



        IncomeRecordDTO result = incomeService.updateIncomeRecord(1L, dto);

        assertEquals(150.0, result.getIncomeAmount());
    }
*/
@Test
void shouldUpdateIncomeRecord() {
    IncomeRecord record = new IncomeRecord();
    record.setId(1L);

    IncomeRecordDTO dto = new IncomeRecordDTO();
    dto.setIncomeAmount(150.0);
    dto.setOrderId(10L);
    dto.setCost(700.);
    dto.setRecordedAt(LocalDateTime.now());
    dto.setNetIncome(1700.);
    dto.setTax(17.);
    dto.setId(1L);

    when(incomeRecordRepository.findById(1L))
            .thenReturn(Optional.of(record));

    when(incomeRecordRepository.save(record))
            .thenReturn(record);

    doAnswer(invocation -> {
        IncomeRecordDTO source = invocation.getArgument(0);
        IncomeRecord target = invocation.getArgument(1);

        target.setIncomeAmount(source.getIncomeAmount());
        target.setOrderId(source.getOrderId());
        target.setCost(source.getCost());
        target.setRecordedAt(source.getRecordedAt());
        target.setNetIncome(source.getNetIncome());
        target.setTax(source.getTax());
        return null;
    }).when(mapper).map(eq(dto), any(IncomeRecord.class));

    when(mapper.map(record, IncomeRecordDTO.class))
            .thenReturn(dto);

    IncomeRecordDTO result = incomeService.updateIncomeRecord(1L, dto);

    assertEquals(150.0, result.getIncomeAmount());
}
    @Test
    void shouldDeleteIncomeRecord() {
        doNothing().when(incomeRecordRepository).deleteById(1L);

        incomeService.deleteIncomeRecord(1L);

        verify(incomeRecordRepository).deleteById(1L);

   /*     Long id = 1L;
        when(incomeRecordRepository.existsById(id)).thenReturn(true);
        doNothing().when(incomeRecordRepository).deleteById(id);
        incomeService.deleteIncomeRecord(id);
        verify(incomeRecordRepository).existsById(id);
        verify(incomeRecordRepository).deleteById(id);*/

    }

    @Test
    void getTotalIncomeByDateRange_shouldReturnCorrectSum() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Double expectedTotal = 1250.75;

        //when(incomeRecordRepository.getTotalIncomeByDateRange(startDateTime, endDateTime))
          //      .thenReturn(expectedTotal);

        // Use Answer to see what's actually passed
        when(incomeRecordRepository.getTotalIncomeByDateRange(
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime actualStart = invocation.getArgument(0);
                    LocalDateTime actualEnd = invocation.getArgument(1);

                    System.out.println("Expected start: " + startDateTime);
                    System.out.println("Actual start: " + actualStart);
                    System.out.println("Expected end: " + endDateTime);
                    System.out.println("Actual end: " + actualEnd);

                    return expectedTotal;
                });

        Double result = incomeService.getTotalIncome(startDate, endDate);

        assertEquals(expectedTotal, result);
        verify(incomeRecordRepository).getTotalIncomeByDateRange(startDateTime, endDateTime);
    }

    @Test
    void getTotalIncomeByDateRange_whenNoRecords_shouldReturnZero() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        when(incomeRecordRepository.getTotalIncomeByDateRange(any(), any()))
                .thenReturn(0.0);

        Double result = incomeService.getTotalIncome(startDate, endDate);

        assertEquals(0.0, result);
        assertNotNull(result);
    }

    @Test
    void getTotalIncomeByDateRange_whenRepositoryReturnsNull_shouldReturnZero() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        when(incomeRecordRepository.getTotalIncomeByDateRange(any(), any()))
                .thenReturn(null);

        Double result = incomeService.getTotalIncome(startDate, endDate);

        assertEquals(0.0, result);
    }
}
