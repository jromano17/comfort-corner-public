package etf.nwt.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import etf.nwt.product.dto.SupplierDTO;
import etf.nwt.product.service.SupplierService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(SupplierController.class)
@DisplayName("Supplier Controller Tests")
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupplierService supplierService;

    private SupplierDTO supplierDTO;

    @BeforeEach
    void setUp() {
        supplierDTO = SupplierDTO.builder()
                .id(1L)
                .name("Premium Furniture Co.")
                .contactEmail("contact@premiumfurniture.com")
                .country("Germany")
                .build();
    }


    @Test
    @DisplayName("POST /api/suppliers - should create supplier and return 201")
    void createSupplier_withValidData_shouldReturn201() throws Exception {
        when(supplierService.createSupplier(any(SupplierDTO.class))).thenReturn(supplierDTO);

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Premium Furniture Co."))
                .andExpect(jsonPath("$.country").value("Germany"));

        verify(supplierService).createSupplier(any(SupplierDTO.class));
    }

    @Test
    @DisplayName("POST /api/suppliers with invalid email - should return 400")
    void createSupplier_withInvalidEmail_shouldReturn400() throws Exception {
        SupplierDTO invalidDTO = SupplierDTO.builder()
                .name("Supplier")
                .contactEmail("invalid-email") // Invalid email format
                .country("USA")
                .build();

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(supplierService, never()).createSupplier(any());
    }


    @Test
    @DisplayName("GET /api/suppliers/{id} - should return supplier")
    void getSupplierById_withExistingId_shouldReturn200() throws Exception {
        when(supplierService.getSupplierById(1L)).thenReturn(supplierDTO);

        mockMvc.perform(get("/api/suppliers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Premium Furniture Co."));

        verify(supplierService).getSupplierById(1L);
    }

    @Test
    @DisplayName("GET /api/suppliers - should return all suppliers")
    void getAllSuppliers_shouldReturn200() throws Exception {
        List<SupplierDTO> suppliers = Arrays.asList(
                supplierDTO,
                SupplierDTO.builder()
                        .id(2L)
                        .name("Another Supplier")
                        .contactEmail("another@email.com")
                        .country("USA")
                        .build()
        );

        when(supplierService.getAllSuppliers()).thenReturn(suppliers);

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Premium Furniture Co."))
                .andExpect(jsonPath("$[1].name").value("Another Supplier"));

        verify(supplierService).getAllSuppliers();
    }

    @Test
    @DisplayName("GET /api/suppliers with includeChairs parameter - should include chairs")
    void getAllSuppliers_withIncludeChairs_shouldIncludeChairs() throws Exception {
        when(supplierService.getAllSuppliersWithChairs()).thenReturn(Arrays.asList(supplierDTO));

        mockMvc.perform(get("/api/suppliers")
                        .param("includeChairs", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(supplierService).getAllSuppliersWithChairs();
    }

    @Test
    @DisplayName("GET /api/suppliers with country parameter - should filter by country")
    void getAllSuppliers_withCountry_shouldFilter() throws Exception {
        when(supplierService.getSuppliersByCountry("Germany")).thenReturn(Arrays.asList(supplierDTO));

        mockMvc.perform(get("/api/suppliers")
                        .param("country", "Germany"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].country").value("Germany"));

        verify(supplierService).getSuppliersByCountry("Germany");
    }

    @Test
    @DisplayName("GET /api/suppliers/email/{email} - should return supplier by email")
    void getSupplierByEmail_shouldReturn200() throws Exception {
        when(supplierService.getSupplierByEmail("contact@premiumfurniture.com")).thenReturn(supplierDTO);

        mockMvc.perform(get("/api/suppliers/email/{email}", "contact@premiumfurniture.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactEmail").value("contact@premiumfurniture.com"));

        verify(supplierService).getSupplierByEmail("contact@premiumfurniture.com");
    }


    @Test
    @DisplayName("PUT /api/suppliers/{id} - should update supplier")
    void updateSupplier_withValidData_shouldReturn200() throws Exception {
        SupplierDTO updatedDTO = SupplierDTO.builder()
                .id(1L)
                .name("Updated Supplier")
                .contactEmail("updated@email.com")
                .country("France")
                .build();

        when(supplierService.updateSupplier(any(SupplierDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/suppliers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Supplier"))
                .andExpect(jsonPath("$.country").value("France"));

        verify(supplierService).updateSupplier(any(SupplierDTO.class));
    }


    @Test
    @DisplayName("PATCH /api/suppliers/{id}/contact-email - should update email")
    void updateSupplierEmail_shouldReturn200() throws Exception {
        SupplierDTO updated = SupplierDTO.builder()
                .id(1L)
                .contactEmail("new@email.com")
                .build();

        when(supplierService.updateSupplierEmail(1L, "new@email.com")).thenReturn(updated);

        mockMvc.perform(patch("/api/suppliers/{id}/contact-email", 1L)
                        .param("email", "new@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactEmail").value("new@email.com"));

        verify(supplierService).updateSupplierEmail(1L, "new@email.com");
    }

    @Test
    @DisplayName("PATCH /api/suppliers/{id}/contact-email with invalid email - should return 400")
    void updateSupplierEmail_withInvalidEmail_shouldReturn400() throws Exception {
        mockMvc.perform(patch("/api/suppliers/{id}/contact-email", 1L)
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest());

        verify(supplierService, never()).updateSupplierEmail(any(), any());
    }

    @Test
    @DisplayName("PATCH /api/suppliers/{id}/country - should update country")
    void updateSupplierCountry_shouldReturn200() throws Exception {
        SupplierDTO updated = SupplierDTO.builder()
                .id(1L)
                .country("France")
                .build();

        when(supplierService.updateSupplierCountry(1L, "France")).thenReturn(updated);

        mockMvc.perform(patch("/api/suppliers/{id}/country", 1L)
                        .param("country", "France"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("France"));

        verify(supplierService).updateSupplierCountry(1L, "France");
    }


    @Test
    @DisplayName("DELETE /api/suppliers/{id} - should delete supplier")
    void deleteSupplier_shouldReturn204() throws Exception {
        doNothing().when(supplierService).deleteSupplier(1L);

        mockMvc.perform(delete("/api/suppliers/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(supplierService).deleteSupplier(1L);
    }

    @Test
    @DisplayName("DELETE /api/suppliers/{id}/force - should force delete supplier")
    void forceDeleteSupplier_shouldReturn204() throws Exception {
        doNothing().when(supplierService).forceDeleteSupplier(1L);

        mockMvc.perform(delete("/api/suppliers/{id}/force", 1L))
                .andExpect(status().isNoContent());

        verify(supplierService).forceDeleteSupplier(1L);
    }


    @Test
    @DisplayName("POST /api/suppliers/{supplierId}/chairs/{chairId} - should add chair to supplier")
    void addChairToSupplier_shouldReturn200() throws Exception {
        when(supplierService.addChairToSupplier(1L, 10L)).thenReturn(supplierDTO);

        mockMvc.perform(post("/api/suppliers/{supplierId}/chairs/{chairId}", 1L, 10L))
                .andExpect(status().isOk());

        verify(supplierService).addChairToSupplier(1L, 10L);
    }

    @Test
    @DisplayName("DELETE /api/suppliers/{supplierId}/chairs/{chairId} - should remove chair from supplier")
    void removeChairFromSupplier_shouldReturn200() throws Exception {
        when(supplierService.removeChairFromSupplier(1L, 10L)).thenReturn(supplierDTO);

        mockMvc.perform(delete("/api/suppliers/{supplierId}/chairs/{chairId}", 1L, 10L))
                .andExpect(status().isOk());

        verify(supplierService).removeChairFromSupplier(1L, 10L);
    }


    @Test
    @DisplayName("GET /api/suppliers/search - should search suppliers")
    void searchSuppliers_shouldReturn200() throws Exception {
        List<SupplierDTO> results = Arrays.asList(supplierDTO);
        when(supplierService.searchSuppliers("premium", false)).thenReturn(results);

        mockMvc.perform(get("/api/suppliers/search")
                        .param("keyword", "premium")
                        .param("includeChairs", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(supplierService).searchSuppliers("premium", false);
    }


    @Test
    @DisplayName("GET /api/suppliers/{id}/chairs/count - should return chair count")
    void getChairCount_shouldReturn200() throws Exception {
        when(supplierService.getChairCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/suppliers/{id}/chairs/count", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(supplierService).getChairCount(1L);
    }

    @Test
    @DisplayName("GET /api/suppliers/stats/country - should return statistics by country")
    void getSupplierStatsByCountry_shouldReturn200() throws Exception {
        // Mock returns some statistics object
        when(supplierService.getSupplierStatsByCountry()).thenReturn(
                java.util.Map.of("Germany", 2L, "USA", 1L)
        );

        mockMvc.perform(get("/api/suppliers/stats/country"))
                .andExpect(status().isOk());

        verify(supplierService).getSupplierStatsByCountry();
    }
}