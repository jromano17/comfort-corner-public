package etf.nwt.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import etf.nwt.product.dto.ColorOptionDTO;
import etf.nwt.product.service.ColorOptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ColorOptionController.class)
@DisplayName("Color Option Controller Tests")
class ColorOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ColorOptionService colorOptionService;

    private ColorOptionDTO colorOptionDTO;

    @BeforeEach
    void setUp() {
        colorOptionDTO = ColorOptionDTO.builder()
                .id(1L)
                .name("Red")
                .hexCode("#FF0000")
                .build();
    }


    @Test
    @DisplayName("POST /api/color-options - should create color option and return 201")
    void createColorOption_withValidData_shouldReturn201() throws Exception {
        when(colorOptionService.createColorOption(any(ColorOptionDTO.class))).thenReturn(colorOptionDTO);

        mockMvc.perform(post("/api/color-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(colorOptionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Red"))
                .andExpect(jsonPath("$.hexCode").value("#FF0000"));

        verify(colorOptionService).createColorOption(any(ColorOptionDTO.class));
    }

    @Test
    @DisplayName("POST /api/color-options with invalid hex code - should return 400")
    void createColorOption_withInvalidHexCode_shouldReturn400() throws Exception {
        ColorOptionDTO invalidDTO = ColorOptionDTO.builder()
                .name("Red")
                .hexCode("FF0000") // Missing # - invalid
                .build();

        mockMvc.perform(post("/api/color-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(colorOptionService, never()).createColorOption(any());
    }


    @Test
    @DisplayName("GET /api/color-options/{id} - should return color option")
    void getColorOptionById_withExistingId_shouldReturn200() throws Exception {
        when(colorOptionService.getColorOptionById(1L)).thenReturn(colorOptionDTO);

        mockMvc.perform(get("/api/color-options/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Red"));

        verify(colorOptionService).getColorOptionById(1L);
    }

    @Test
    @DisplayName("GET /api/color-options/name/{name} - should return color option by name")
    void getColorOptionByName_shouldReturn200() throws Exception {
        when(colorOptionService.getColorOptionByName("Red")).thenReturn(colorOptionDTO);

        mockMvc.perform(get("/api/color-options/name/{name}", "Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Red"));

        verify(colorOptionService).getColorOptionByName("Red");
    }

    @Test
    @DisplayName("GET /api/color-options/hex/{hexCode} - should return color option by hex code")
    void getColorOptionByHexCode_shouldReturn200() throws Exception {
        when(colorOptionService.getColorOptionByHexCode("#FF0000")).thenReturn(colorOptionDTO);

        mockMvc.perform(get("/api/color-options/hex/{hexCode}", "#FF0000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hexCode").value("#FF0000"));

        verify(colorOptionService).getColorOptionByHexCode("#FF0000");
    }

    @Test
    @DisplayName("GET /api/color-options - should return all color options")
    void getAllColorOptions_shouldReturn200() throws Exception {
        List<ColorOptionDTO> colorOptions = Arrays.asList(
                colorOptionDTO,
                ColorOptionDTO.builder()
                        .id(2L)
                        .name("Blue")
                        .hexCode("#0000FF")
                        .build()
        );

        when(colorOptionService.getAllColorOptions()).thenReturn(colorOptions);

        mockMvc.perform(get("/api/color-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Red"))
                .andExpect(jsonPath("$[1].name").value("Blue"));

        verify(colorOptionService).getAllColorOptions();
    }


    @Test
    @DisplayName("GET /api/color-options/search - should search color options")
    void searchColorOptions_shouldReturn200() throws Exception {
        List<ColorOptionDTO> results = Arrays.asList(colorOptionDTO);
        when(colorOptionService.searchColorOptions("red")).thenReturn(results);

        mockMvc.perform(get("/api/color-options/search")
                        .param("keyword", "red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Red"));

        verify(colorOptionService).searchColorOptions("red");
    }


    @Test
    @DisplayName("PUT /api/color-options/{id} - should update color option")
    void updateColorOption_withValidData_shouldReturn200() throws Exception {
        ColorOptionDTO updatedDTO = ColorOptionDTO.builder()
                .id(1L)
                .name("Updated Red")
                .hexCode("#FF1111")
                .build();

        when(colorOptionService.updateColorOption(eq(1L), any(ColorOptionDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/color-options/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Red"));

        verify(colorOptionService).updateColorOption(eq(1L), any(ColorOptionDTO.class));
    }




    @Test
    @DisplayName("DELETE /api/color-options/{id} - should delete color option")
    void deleteColorOption_shouldReturn204() throws Exception {
        doNothing().when(colorOptionService).deleteColorOption(1L);

        mockMvc.perform(delete("/api/color-options/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(colorOptionService).deleteColorOption(1L);
    }


    @Test
    @DisplayName("Create with invalid hex format - should return 400")
    void createColorOption_withWrongHexFormat_shouldReturn400() throws Exception {
        ColorOptionDTO invalidDTO = ColorOptionDTO.builder()
                .name("Test")
                .hexCode("RED") // Invalid hex format
                .build();

        mockMvc.perform(post("/api/color-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(colorOptionService, never()).createColorOption(any());
    }
}