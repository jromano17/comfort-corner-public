package etf.nwt.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import etf.nwt.product.dto.MaterialDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.service.MaterialService;
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
@WebMvcTest(MaterialController.class)
@DisplayName("Material Controller Tests")
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MaterialService materialService;

    private MaterialDTO materialDTO;

    @BeforeEach
    void setUp() {
        materialDTO = MaterialDTO.builder()
                .id(1L)
                .name("Leather")
                .description("Premium leather material")
                .build();
    }


    @Test
    @DisplayName("POST /api/materials - should create material and return 201")
    void createMaterial_withValidData_shouldReturn201() throws Exception {
        when(materialService.createMaterial(any(MaterialDTO.class))).thenReturn(materialDTO);

        mockMvc.perform(post("/api/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(materialDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Leather"))
                .andExpect(jsonPath("$.description").value("Premium leather material"));

        verify(materialService).createMaterial(any(MaterialDTO.class));
    }

    @Test
    @DisplayName("POST /api/materials with invalid data - should return 400")
    void createMaterial_withInvalidData_shouldReturn400() throws Exception {
        MaterialDTO invalidDTO = MaterialDTO.builder()
                .name("")
                .build();

        mockMvc.perform(post("/api/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(materialService, never()).createMaterial(any());
    }


    @Test
    @DisplayName("GET /api/materials/{id} - should return material")
    void getMaterialById_withExistingId_shouldReturn200() throws Exception {
        when(materialService.getMaterialById(1L)).thenReturn(materialDTO);

        mockMvc.perform(get("/api/materials/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Leather"));

        verify(materialService).getMaterialById(1L);
    }

    @Test
    @DisplayName("GET /api/materials/{id} with non-existent ID - should return 404")
    void getMaterialById_withNonExistentId_shouldReturn404() throws Exception {
        when(materialService.getMaterialById(999L))
                .thenThrow(new ResourceNotFoundException("Material not found with id: 999"));

        mockMvc.perform(get("/api/materials/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(materialService).getMaterialById(999L);
    }

    @Test
    @DisplayName("GET /api/materials - should return all materials")
    void getAllMaterials_shouldReturn200() throws Exception {
        List<MaterialDTO> materials = Arrays.asList(
                materialDTO,
                MaterialDTO.builder()
                        .id(2L)
                        .name("Fabric")
                        .description("Soft fabric material")
                        .build()
        );

        when(materialService.getAllMaterials()).thenReturn(materials);

        mockMvc.perform(get("/api/materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Leather"))
                .andExpect(jsonPath("$[1].name").value("Fabric"));

        verify(materialService).getAllMaterials();
    }

    @Test
    @DisplayName("GET /api/materials/name/{name} - should return material by name")
    void getMaterialByName_shouldReturn200() throws Exception {
        when(materialService.getMaterialByName("Leather")).thenReturn(materialDTO);

        mockMvc.perform(get("/api/materials/name/{name}", "Leather"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Leather"));

        verify(materialService).getMaterialByName("Leather");
    }

    @Test
    @DisplayName("GET /api/materials/search - should search materials")
    void searchMaterials_shouldReturn200() throws Exception {
        List<MaterialDTO> results = Arrays.asList(materialDTO);
        when(materialService.searchMaterials("leather")).thenReturn(results);

        mockMvc.perform(get("/api/materials/search")
                        .param("keyword", "leather"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Leather"));

        verify(materialService).searchMaterials("leather");
    }


    @Test
    @DisplayName("PUT /api/materials/{id} - should update material")
    void updateMaterial_withValidData_shouldReturn200() throws Exception {
        MaterialDTO updatedDTO = MaterialDTO.builder()
                .id(1L)
                .name("Updated Material")
                .description("Updated description")
                .build();

        when(materialService.updateMaterial(eq(1L), any(MaterialDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/materials/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Material"));

        verify(materialService).updateMaterial(eq(1L), any(MaterialDTO.class));
    }


    @Test
    @DisplayName("DELETE /api/materials/{id} - should delete material")
    void deleteMaterial_shouldReturn204() throws Exception {
        doNothing().when(materialService).deleteMaterial(1L);

        mockMvc.perform(delete("/api/materials/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(materialService).deleteMaterial(1L);
    }


    @Test
    @DisplayName("Update with ID mismatch - should return 400")
    void updateMaterial_withIdMismatch_shouldReturn400() throws Exception {
        MaterialDTO dto = MaterialDTO.builder()
                .id(2L)
                .name("Material")
                .build();

        mockMvc.perform(put("/api/materials/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(materialService, never()).updateMaterial(any(), any());
    }
}