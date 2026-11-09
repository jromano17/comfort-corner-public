package etf.nwt.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import etf.nwt.product.config.TestConfig;
import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.service.ChairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ChairController.class)
@Import({TestConfig.class})//, ModelMapperConfig.class})
@DisplayName("Chair Controller Tests")
class ChairControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChairService chairService;

    private ChairDTO chairDTO;

    @BeforeEach
    void setUp() {
        chairDTO = ChairDTO.builder()
                .id(1L)
                .name("Ergonomic Office Chair")
                .description("Comfortable office chair")
                .basePrice(new BigDecimal("199.99"))
                .categoryId(1L)
                .supplierId(1L)
                .build();
    }


    @Test
    @DisplayName("POST /api/chairs - should create chair and return 201")
    void createChair_withValidData_shouldReturn201() throws Exception {
        when(chairService.createChair(any(ChairDTO.class))).thenReturn(chairDTO);

        mockMvc.perform(post("/api/chairs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chairDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ergonomic Office Chair"))
                .andExpect(jsonPath("$.basePrice").value(199.99))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.supplierId").value(1));

        verify(chairService).createChair(any(ChairDTO.class));
    }

    @Test
    @DisplayName("POST /api/chairs with invalid data - should return 400")
    void createChair_withInvalidData_shouldReturn400() throws Exception {
        ChairDTO invalidDTO = ChairDTO.builder()
                .name("") // Empty name - invalid
                .basePrice(new BigDecimal("-100"))
                .build();

        mockMvc.perform(post("/api/chairs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));

        verify(chairService, never()).createChair(any());
    }

    @Test
    @DisplayName("POST /api/chairs with missing required fields - should return 400")
    void createChair_withMissingFields_shouldReturn400() throws Exception {
        String json = "{\"description\":\"Some description\"}";

        mockMvc.perform(post("/api/chairs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("GET /api/chairs/{id} - should return chair")
    void getChairById_withExistingId_shouldReturn200() throws Exception {
        when(chairService.getChairById(1L)).thenReturn(chairDTO);

        mockMvc.perform(get("/api/chairs/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ergonomic Office Chair"));

        verify(chairService).getChairById(1L);
    }

    @Test
    @DisplayName("GET /api/chairs/{id} with non-existent ID - should return 404")
    void getChairById_withNonExistentId_shouldReturn404() throws Exception {
        when(chairService.getChairById(999L))
                .thenThrow(new ResourceNotFoundException("Chair not found with id: 999"));

        mockMvc.perform(get("/api/chairs/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"))
                .andExpect(jsonPath("$.message").value("Chair not found with id: 999"));

        verify(chairService).getChairById(999L);
    }


    @Test
    @DisplayName("GET /api/chairs - should return all chairs")
    void getAllChairs_shouldReturn200() throws Exception {
        List<ChairDTO> chairs = Arrays.asList(
                chairDTO,
                ChairDTO.builder()
                        .id(2L)
                        .name("Gaming Chair")
                        .basePrice(new BigDecimal("299.99"))
                        .categoryId(2L)
                        .supplierId(2L)
                        .build()
        );

        when(chairService.getAllChairs()).thenReturn(chairs);

        mockMvc.perform(get("/api/chairs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ergonomic Office Chair"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Gaming Chair"));

        verify(chairService).getAllChairs();
    }

    @Test
    @DisplayName("GET /api/chairs when empty - should return empty array")
    void getAllChairs_whenEmpty_shouldReturnEmptyArray() throws Exception {
        when(chairService.getAllChairs()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/chairs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(chairService).getAllChairs();
    }

    @Test
    @DisplayName("PUT /api/chairs/{id} - should update chair")
    void updateChair_withValidData_shouldReturn200() throws Exception {
        ChairDTO updatedDTO = ChairDTO.builder()
                .id(1L)
                .name("Updated Chair Name")
                .description("Updated chair descr")
                .basePrice(new BigDecimal("249.99"))
                .categoryId(1L)
                .supplierId(1L)
                .build();

        when(chairService.updateChair(any(ChairDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/chairs/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Chair Name"))
                .andExpect(jsonPath("$.basePrice").value(249.99));

        verify(chairService).updateChair(any(ChairDTO.class));
    }

    @Test
    @DisplayName("PUT /api/chairs/{id} with ID mismatch - should return 400")
    void updateChair_withIdMismatch_shouldReturn400() throws Exception {
        ChairDTO dto = ChairDTO.builder()
                .id(2L)
                .name("Updated Chair")
                .build();

        mockMvc.perform(put("/api/chairs/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(chairService, never()).updateChair(any());
    }


    @Test
    @DisplayName("DELETE /api/chairs/{id} - should delete chair")
    void deleteChair_withExistingId_shouldReturn204() throws Exception {
        doNothing().when(chairService).deleteChair(1L);

        mockMvc.perform(delete("/api/chairs/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(chairService).deleteChair(1L);
    }

    @Test
    @DisplayName("DELETE /api/chairs/{id} with non-existent ID - should return 404")
    void deleteChair_withNonExistentId_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Chair not found with id: 999"))
                .when(chairService).deleteChair(999L);

        mockMvc.perform(delete("/api/chairs/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(chairService).deleteChair(999L);
    }

    @Test
    @DisplayName("Invalid JSON in request - should return 400")
    void createChair_withInvalidJson_shouldReturn400() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(post("/api/chairs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Wrong content type - should return 415")
    void createChair_withWrongContentType_shouldReturn415() throws Exception {
        mockMvc.perform(post("/api/chairs")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Method not allowed - should return 405")
    void methodNotAllowed_shouldReturn405() throws Exception {
        mockMvc.perform(patch("/api/chairs"))
                .andExpect(status().isMethodNotAllowed());
    }


    @Test
    @DisplayName("Chair name too long - should return 400")
    void createChair_withNameTooLong_shouldReturn400() throws Exception {
        String longName = "A".repeat(101); // 101 characters
        ChairDTO dto = ChairDTO.builder()
                .name(longName)
                .basePrice(new BigDecimal("199.99"))
                .categoryId(1L)
                .description("some descr")
                .supplierId(1L)
                .build();

        mockMvc.perform(post("/api/chairs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.name").exists());
    }

    @Test
    @DisplayName("Negative price - should return 400")
    void createChair_withNegativePrice_shouldReturn400() throws Exception {
        ChairDTO dto = ChairDTO.builder()
                .name("Chair")
                .basePrice(new BigDecimal("-100.00"))
                .categoryId(1L)
                .supplierId(1L)
                .build();

        mockMvc.perform(post("/api/chairs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.basePrice").exists());
    }
}