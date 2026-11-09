package etf.nwt.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import etf.nwt.product.dto.CategoryDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.service.CategoryService;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(CategoryController.class)
@DisplayName("Category Controller Tests")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Office Furniture")
                .description("Chairs and desks for office")
                .build();
    }


    @Test
    @DisplayName("POST /api/categories - should create category and return 201")
    void createCategory_withValidData_shouldReturn201() throws Exception {
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(categoryDTO);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Office Furniture"));

        verify(categoryService).createCategory(any(CategoryDTO.class));
    }

    @Test
    @DisplayName("POST /api/categories with invalid data - should return 400")
    void createCategory_withInvalidData_shouldReturn400() throws Exception {
        CategoryDTO invalidDTO = CategoryDTO.builder()
                .name("") // Empty name
                .build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any());
    }


    @Test
    @DisplayName("GET /api/categories/{id} - should return category")
    void getCategoryById_withExistingId_shouldReturn200() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Office Furniture"));

        verify(categoryService).getCategoryById(1L);
    }

    @Test
    @DisplayName("GET /api/categories/{id} with non-existent ID - should return 404")
    void getCategoryById_withNonExistentId_shouldReturn404() throws Exception {
        when(categoryService.getCategoryById(999L))
                .thenThrow(new ResourceNotFoundException("Category not found with id: 999"));

        mockMvc.perform(get("/api/categories/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(categoryService).getCategoryById(999L);
    }

    @Test
    @DisplayName("GET /api/categories - should return all categories")
    void getAllCategories_shouldReturn200() throws Exception {
        List<CategoryDTO> categories = Arrays.asList(
                categoryDTO,
                CategoryDTO.builder()
                        .id(2L)
                        .name("Gaming Chairs")
                        .description("Chairs for gamers")
                        .build()
        );

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Office Furniture"))
                .andExpect(jsonPath("$[1].name").value("Gaming Chairs"));

        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("GET /api/categories with includeChairs parameter - should include chairs")
    void getAllCategories_withIncludeChairs_shouldIncludeChairs() throws Exception {
        when(categoryService.getAllCategoriesWithChairs()).thenReturn(Arrays.asList(categoryDTO));

        mockMvc.perform(get("/api/categories")
                        .param("includeChairs", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(categoryService).getAllCategoriesWithChairs();
    }


    @Test
    @DisplayName("PUT /api/categories/{id} - should update category")
    void updateCategory_withValidData_shouldReturn200() throws Exception {
        CategoryDTO updatedDTO = CategoryDTO.builder()
                .id(1L)
                .name("Updated Category")
                .description("Updated description")
                .build();

        when(categoryService.updateCategory(any(CategoryDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Category"));

        verify(categoryService).updateCategory(any(CategoryDTO.class));
    }


    @Test
    @DisplayName("DELETE /api/categories/{id} - should delete category")
    void deleteCategory_shouldReturn204() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    @DisplayName("DELETE /api/categories/{id}/force - should force delete")
    void forceDeleteCategory_shouldReturn204() throws Exception {
        doNothing().when(categoryService).forceDeleteCategory(1L);

        mockMvc.perform(delete("/api/categories/{id}/force", 1L))
                .andExpect(status().isNoContent());

        verify(categoryService).forceDeleteCategory(1L);
    }


    @Test
    @DisplayName("PATCH /api/categories/{id}/name - should update name")
    void updateCategoryName_shouldReturn200() throws Exception {
        CategoryDTO updated = CategoryDTO.builder()
                .id(1L)
                .name("New Name")
                .build();

        when(categoryService.updateCategoryName(1L, "New Name")).thenReturn(updated);

        mockMvc.perform(patch("/api/categories/{id}/name", 1L)
                        .param("name", "New Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));

        verify(categoryService).updateCategoryName(1L, "New Name");
    }


    @Test
    @DisplayName("POST /api/categories/{categoryId}/chairs/{chairId} - should add chair to category")
    void addChairToCategory_shouldReturn200() throws Exception {
        when(categoryService.addChairToCategory(1L, 10L)).thenReturn(categoryDTO);

        mockMvc.perform(post("/api/categories/{categoryId}/chairs/{chairId}", 1L, 10L))
                .andExpect(status().isOk());

        verify(categoryService).addChairToCategory(1L, 10L);
    }

    @Test
    @DisplayName("DELETE /api/categories/{categoryId}/chairs/{chairId} - should remove chair from category")
    void removeChairFromCategory_shouldReturn200() throws Exception {
        when(categoryService.removeChairFromCategory(1L, 10L)).thenReturn(categoryDTO);

        mockMvc.perform(delete("/api/categories/{categoryId}/chairs/{chairId}", 1L, 10L))
                .andExpect(status().isOk());

        verify(categoryService).removeChairFromCategory(1L, 10L);
    }


    @Test
    @DisplayName("GET /api/categories/search - should search categories")
    void searchCategories_shouldReturn200() throws Exception {
        List<CategoryDTO> results = Arrays.asList(categoryDTO);
        when(categoryService.searchCategories("office")).thenReturn(results);

        mockMvc.perform(get("/api/categories/search")
                        .param("keyword", "office"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(categoryService).searchCategories("office");
    }
}