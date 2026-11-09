package etf.nwt.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import etf.nwt.order.dto.incomeRecord.IncomeRecordDTO;
import etf.nwt.order.service.IncomeRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(IncomeRecordController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.fail-fast=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class IncomeRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncomeRecordService incomeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRecordIncome() throws Exception {
        IncomeRecordDTO dto = new IncomeRecordDTO();
        dto.setId(1L);
        dto.setOrderId(10L);
        dto.setIncomeAmount(100.0);
        dto.setCost(700.);
        dto.setRecordedAt(LocalDateTime.now());
        dto.setNetIncome(1700.);
        dto.setTax(17.);

        when(incomeService.recordIncome(10L)).thenReturn(dto);

        mockMvc.perform(post("/api/income/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderId").value(10L));
    }

    @Test
    void shouldGetAllIncomeRecords() throws Exception {
        IncomeRecordDTO dto = new IncomeRecordDTO();
        dto.setId(1L);

        when(incomeService.getAllIncomeRecords()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldGetIncomeById() throws Exception {
        IncomeRecordDTO dto = new IncomeRecordDTO();
        dto.setId(1L);

        when(incomeService.getIncomeRecord(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/income/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldDeleteIncome() throws Exception {
        doNothing().when(incomeService).deleteIncomeRecord(1L);

        mockMvc.perform(delete("/api/income/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateIncome() throws Exception {
        IncomeRecordDTO dto = new IncomeRecordDTO();
        dto.setId(1L);
        dto.setIncomeAmount(200.0);
        dto.setCost(700.);
        dto.setRecordedAt(LocalDateTime.now());
        dto.setNetIncome(1700.);
        dto.setTax(17.);
        dto.setOrderId(1L);

        when(incomeService.updateIncomeRecord(eq(1L), any()))
                .thenReturn(dto);

        mockMvc.perform(patch("/api/income/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incomeAmount").value(200.0));
    }

    @Test
    void getTotalIncomeByDateRange_shouldReturnTotalIncome() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Double totalIncome = 1250.75;

        when(incomeService.getTotalIncome(startDate, endDate))
                .thenReturn(totalIncome);

        mockMvc.perform(get("/api/income/total")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("1250.75"));

        verify(incomeService).getTotalIncome(startDate, endDate);
    }

    @Test
    void getTotalIncomeByDateRange_whenInvalidDateRange_shouldReturnBadRequest() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        mockMvc.perform(get("/api/income/total")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isBadRequest());

        verify(incomeService, never()).getTotalIncome(any(), any());
    }

    @Test
    void getTotalIncomeByDateRange_whenNoIncome_shouldReturnZero() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        when(incomeService.getTotalIncome(startDate, endDate))
                .thenReturn(0.0);

        mockMvc.perform(get("/api/income/total")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }
}
