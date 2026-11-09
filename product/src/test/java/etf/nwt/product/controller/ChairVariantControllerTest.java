package etf.nwt.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import etf.nwt.product.dto.ChairVariantDTO;
import etf.nwt.product.model.*;
import etf.nwt.product.service.ChairVariantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
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
@WebMvcTest(ChairVariantController.class)
@DisplayName("Chair Variant Controller Tests")
class ChairVariantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChairVariantService chairVariantService;

    private ChairVariantDTO variantDTO;
    private Material material;
    private ColorOption colorOption;
    private Dimension dimension;

    @BeforeEach
    void setUp() {
        material = Material.builder()
                .id(1L)
                .name("Leather")
                .build();

        colorOption = ColorOption.builder()
                .id(1L)
                .name("Black")
                .hexCode("#000000")
                .build();

        dimension = Dimension.builder()
                .id(1L)
                .name("Medium")
                .width(60.0)
                .height(110.0)
                .depth(60.0)
                .build();

        variantDTO = ChairVariantDTO.builder()
                .id(1L)
                .chairId(1L)
                .material(material)
                .colorOption(colorOption)
                .dimension(dimension)
                .finalPrice(new BigDecimal("299.99"))
                .stockQuantity(10)
                .build();
    }


    @Test
    @DisplayName("POST /api/chair-variants - should create variant and return 201")
    void createChairVariant_withValidData_shouldReturn201() throws Exception {
        when(chairVariantService.createChairVariant(any(ChairVariantDTO.class))).thenReturn(variantDTO);

        mockMvc.perform(post("/api/chair-variants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(variantDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.finalPrice").value(299.99))
                .andExpect(jsonPath("$.stockQuantity").value(10));

        verify(chairVariantService).createChairVariant(any(ChairVariantDTO.class));
    }

    @Test
    @DisplayName("POST /api/chair-variants with invalid data - should return 400")
    void createChairVariant_withInvalidData_shouldReturn400() throws Exception {
        ChairVariantDTO invalidDTO = ChairVariantDTO.builder()
                .chairId(null) // Missing required field
                .finalPrice(new BigDecimal("-100")) // Negative price
                .stockQuantity(-5) // Negative stock
                .build();

        mockMvc.perform(post("/api/chair-variants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(chairVariantService, never()).createChairVariant(any());
    }


    @Test
    @DisplayName("GET /api/chair-variants/{id} - should return variant")
    void getChairVariantById_withExistingId_shouldReturn200() throws Exception {
        when(chairVariantService.getChairVariantById(1L)).thenReturn(variantDTO);

        mockMvc.perform(get("/api/chair-variants/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.chairId").value(1))
                .andExpect(jsonPath("$.finalPrice").value(299.99));

        verify(chairVariantService).getChairVariantById(1L);
    }

    @Test
    @DisplayName("GET /api/chair-variants - should return all variants")
    void getAllChairVariants_shouldReturn200() throws Exception {
        List<ChairVariantDTO> variants = Arrays.asList(
                variantDTO,
                ChairVariantDTO.builder()
                        .id(2L)
                        .chairId(1L)
                        .finalPrice(new BigDecimal("349.99"))
                        .stockQuantity(5)
                        .build()
        );

        when(chairVariantService.getAllChairVariants()).thenReturn(variants);

        mockMvc.perform(get("/api/chair-variants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(chairVariantService).getAllChairVariants();
    }

    @Test
    @DisplayName("GET /api/chair-variants/chair/{chairId} - should return variants by chair")
    void getVariantsByChairId_shouldReturn200() throws Exception {
        List<ChairVariantDTO> variants = Arrays.asList(variantDTO);
        when(chairVariantService.getVariantsByChairId(1L)).thenReturn(variants);

        mockMvc.perform(get("/api/chair-variants/chair/{chairId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].chairId").value(1));

        verify(chairVariantService).getVariantsByChairId(1L);
    }

    @Test
    @DisplayName("GET /api/chair-variants/in-stock - should return in-stock variants")
    void getInStockVariants_shouldReturn200() throws Exception {
        List<ChairVariantDTO> variants = Arrays.asList(variantDTO);
        when(chairVariantService.getInStockVariants()).thenReturn(variants);

        mockMvc.perform(get("/api/chair-variants/in-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stockQuantity").value(10));

        verify(chairVariantService).getInStockVariants();
    }


    @Test
    @DisplayName("PUT /api/chair-variants/{id} - should update variant")
    void updateChairVariant_withValidData_shouldReturn200() throws Exception {
        ChairVariantDTO updatedDTO = ChairVariantDTO.builder()
                .id(1L)
                .chairId(1L)
                .finalPrice(new BigDecimal("349.99"))
                .material(material)
                .colorOption(colorOption)
                .dimension(dimension)
                .stockQuantity(20)
                .build();

        when(chairVariantService.updateChairVariant(any(ChairVariantDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/chair-variants/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalPrice").value(349.99))
                .andExpect(jsonPath("$.stockQuantity").value(20));

        verify(chairVariantService).updateChairVariant(any(ChairVariantDTO.class));
    }


    @Test
    @DisplayName("PATCH /api/chair-variants/{id}/stock - should update stock quantity")
    void updateStockQuantity_shouldReturn200() throws Exception {
        ChairVariantDTO updated = ChairVariantDTO.builder()
                .id(1L)
                .stockQuantity(50)
                .build();

        when(chairVariantService.updateStockQuantity(1L, 50)).thenReturn(updated);

        mockMvc.perform(patch("/api/chair-variants/{id}/stock", 1L)
                        .param("quantity", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(50));

        verify(chairVariantService).updateStockQuantity(1L, 50);
    }


    @Test
    @DisplayName("DELETE /api/chair-variants/{id} - should delete variant")
    void deleteChairVariant_shouldReturn204() throws Exception {
        doNothing().when(chairVariantService).deleteChairVariant(1L);

        mockMvc.perform(delete("/api/chair-variants/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(chairVariantService).deleteChairVariant(1L);
    }

    @Test
    @DisplayName("DELETE /api/chair-variants/chair/{chairId} - should delete variants by chair")
    void deleteVariantsByChairId_shouldReturn204() throws Exception {
        doNothing().when(chairVariantService).deleteVariantsByChairId(1L);

        mockMvc.perform(delete("/api/chair-variants/chair/{chairId}", 1L))
                .andExpect(status().isNoContent());

        verify(chairVariantService).deleteVariantsByChairId(1L);
    }


    @Test
    @DisplayName("Update with negative stock - should return 400")
    void updateStockQuantity_withNegativeStock_shouldReturn400() throws Exception {
        mockMvc.perform(patch("/api/chair-variants/{id}/stock", 1L)
                        .param("quantity", "-10"))
                .andExpect(status().isBadRequest());

        verify(chairVariantService, never()).updateStockQuantity(any(), any());
    }
}