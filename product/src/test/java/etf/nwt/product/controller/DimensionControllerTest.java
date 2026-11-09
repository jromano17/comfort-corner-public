package etf.nwt.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import etf.nwt.product.dto.DimensionDTO;
import etf.nwt.product.exception.GlobalExceptionHandler;
import etf.nwt.product.service.DimensionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DimensionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DimensionService dimensionService;

    @InjectMocks
    private DimensionController dimensionController;

    private ObjectMapper objectMapper;
    private DimensionDTO dimensionDTO;
    private final Long DIMENSION_ID = 1L;
    private final String DIMENSION_NAME = "Standard Box";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dimensionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        dimensionDTO = DimensionDTO.builder()
                .id(DIMENSION_ID)
                .name(DIMENSION_NAME)
                .width(30.0)
                .height(20.0)
                .depth(15.0)
                .weightCapacity(25.0)
                .build();
    }

    @Test
    void createDimension_Success() throws Exception {
        when(dimensionService.createDimension(any(DimensionDTO.class))).thenReturn(dimensionDTO);

        mockMvc.perform(post("/api/dimensions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dimensionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(DIMENSION_ID))
                .andExpect(jsonPath("$.name").value(DIMENSION_NAME))
                .andExpect(jsonPath("$.width").value(30.0))
                .andExpect(jsonPath("$.height").value(20.0))
                .andExpect(jsonPath("$.depth").value(15.0))
                .andExpect(jsonPath("$.weightCapacity").value(25.0));

        verify(dimensionService).createDimension(any(DimensionDTO.class));
    }

    @Test
    void createDimension_WithInvalidData_ReturnsBadRequest() throws Exception {
        DimensionDTO invalidDTO = DimensionDTO.builder()
                .name("")
                .width(-10.0)
                .build();

        mockMvc.perform(post("/api/dimensions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(dimensionService, never()).createDimension(any());
    }

    @Test
    void getDimensionById_Success() throws Exception {
        when(dimensionService.getDimensionById(DIMENSION_ID)).thenReturn(dimensionDTO);

        mockMvc.perform(get("/api/dimensions/{id}", DIMENSION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DIMENSION_ID))
                .andExpect(jsonPath("$.name").value(DIMENSION_NAME));

        verify(dimensionService).getDimensionById(DIMENSION_ID);
    }

    @Test
    void getDimensionByName_Success() throws Exception {
        when(dimensionService.getDimensionByName(DIMENSION_NAME)).thenReturn(dimensionDTO);

        mockMvc.perform(get("/api/dimensions/name/{name}", DIMENSION_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DIMENSION_ID))
                .andExpect(jsonPath("$.name").value(DIMENSION_NAME));

        verify(dimensionService).getDimensionByName(DIMENSION_NAME);
    }

    @Test
    void getAllDimensions_Success() throws Exception {
        List<DimensionDTO> dimensions = Arrays.asList(dimensionDTO);
        when(dimensionService.getAllDimensions()).thenReturn(dimensions);

        mockMvc.perform(get("/api/dimensions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(DIMENSION_ID))
                .andExpect(jsonPath("$[0].name").value(DIMENSION_NAME));

        verify(dimensionService).getAllDimensions();
    }

    @Test
    void getAllDimensions_WhenEmpty_ReturnsEmptyList() throws Exception {
        when(dimensionService.getAllDimensions()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/dimensions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(dimensionService).getAllDimensions();
    }

    @Test
    void searchDimensions_Success() throws Exception {
        String keyword = "box";
        List<DimensionDTO> dimensions = Arrays.asList(dimensionDTO);
        when(dimensionService.searchDimensions(keyword)).thenReturn(dimensions);

        mockMvc.perform(get("/api/dimensions/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(DIMENSION_ID))
                .andExpect(jsonPath("$[0].name").value(DIMENSION_NAME));

        verify(dimensionService).searchDimensions(keyword);
    }

    @Test
    void getDimensionsBySizeRange_Success() throws Exception {
        List<DimensionDTO> dimensions = Arrays.asList(dimensionDTO);
        when(dimensionService.getDimensionsBySizeRange(
                anyDouble(), anyDouble(),
                anyDouble(), anyDouble(),
                anyDouble(), anyDouble())).thenReturn(dimensions);

        mockMvc.perform(get("/api/dimensions/size-range")
                        .param("minWidth", "20.0")
                        .param("maxWidth", "40.0")
                        .param("minHeight", "15.0")
                        .param("maxHeight", "25.0")
                        .param("minDepth", "10.0")
                        .param("maxDepth", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(DIMENSION_ID))
                .andExpect(jsonPath("$[0].name").value(DIMENSION_NAME));

        verify(dimensionService).getDimensionsBySizeRange(
                20.0, 40.0,
                15.0, 25.0,
                10.0, 20.0);
    }

    @Test
    void getDimensionsByMinWeightCapacity_Success() throws Exception {
        List<DimensionDTO> dimensions = Arrays.asList(dimensionDTO);
        when(dimensionService.getDimensionsByMinWeightCapacity(anyDouble())).thenReturn(dimensions);

        mockMvc.perform(get("/api/dimensions/weight-capacity")
                        .param("minWeightCapacity", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(DIMENSION_ID))
                .andExpect(jsonPath("$[0].name").value(DIMENSION_NAME));

        verify(dimensionService).getDimensionsByMinWeightCapacity(20.0);
    }

    @Test
    void getSizeStatistics_Success() throws Exception {
        Object[] statistics = new Object[]{10.0, 50.0, 30.0, 5.0, 30.0, 15.0, 2.0, 20.0, 10.0};
        when(dimensionService.getSizeStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/api/dimensions/stats/sizes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(10.0))
                .andExpect(jsonPath("$[1]").value(50.0))
                .andExpect(jsonPath("$[2]").value(30.0));

        verify(dimensionService).getSizeStatistics();
    }

    @Test
    void updateDimension_Success() throws Exception {
        when(dimensionService.updateDimension(eq(DIMENSION_ID), any(DimensionDTO.class))).thenReturn(dimensionDTO);

        mockMvc.perform(put("/api/dimensions/{id}", DIMENSION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dimensionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DIMENSION_ID))
                .andExpect(jsonPath("$.name").value(DIMENSION_NAME));

        verify(dimensionService).updateDimension(eq(DIMENSION_ID), any(DimensionDTO.class));
    }

    @Test
    void updateDimension_WithInvalidData_ReturnsBadRequest() throws Exception {
        DimensionDTO invalidDTO = DimensionDTO.builder()
                .name("")
                .width(-10.0)
                .build();

        mockMvc.perform(put("/api/dimensions/{id}", DIMENSION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(dimensionService, never()).updateDimension(anyLong(), any());
    }

    @Test
    void deleteDimension_Success() throws Exception {
        doNothing().when(dimensionService).deleteDimension(DIMENSION_ID);

        mockMvc.perform(delete("/api/dimensions/{id}", DIMENSION_ID))
                .andExpect(status().isNoContent());

        verify(dimensionService).deleteDimension(DIMENSION_ID);
    }


}