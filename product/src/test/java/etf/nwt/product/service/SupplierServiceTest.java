package etf.nwt.product.service;

import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.dto.SupplierDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.model.Supplier;
import etf.nwt.product.model.Chair;
import etf.nwt.product.repository.SupplierRepository;
import etf.nwt.product.repository.ChairRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Supplier Service Tests")
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ChairRepository chairRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierDTO supplierDTO;
    private Chair chair1;
    private Chair chair2;

    @BeforeEach
    void setUp() {
        chair1 = Chair.builder()
                .id(1L)
                .name("Office Chair")
                .basePrice(new BigDecimal("199.99"))
                .build();

        chair2 = Chair.builder()
                .id(2L)
                .name("Gaming Chair")
                .basePrice(new BigDecimal("299.99"))
                .build();

        supplier = Supplier.builder()
                .id(1L)
                .name("Premium Furniture Co.")
                .contactEmail("contact@premiumfurniture.com")
                .country("Germany")
                .chairs(new ArrayList<>(Arrays.asList(chair1, chair2)))
                .build();

        supplierDTO = SupplierDTO.builder()
                .id(1L)
                .name("Premium Furniture Co.")
                .contactEmail("contact@premiumfurniture.com")
                .country("Germany")
                .build();
    }


    @Test
    @DisplayName("Create supplier with valid data - should succeed")
    void createSupplier_withValidData_shouldReturnSupplierDTO() {
        SupplierDTO requestDTO = SupplierDTO.builder()
                .name("New Supplier")
                .contactEmail("new@supplier.com")
                .country("USA")
                .build();

        when(supplierRepository.existsByContactEmailIgnoreCase("new@supplier.com")).thenReturn(false);
        when(modelMapper.map(requestDTO, Supplier.class)).thenReturn(supplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.createSupplier(requestDTO);

        assertNotNull(result);
        assertEquals("Premium Furniture Co.", result.getName());
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Create supplier with duplicate email - should throw exception")
    void createSupplier_withDuplicateEmail_shouldThrowException() {
        SupplierDTO requestDTO = SupplierDTO.builder()
                .name("New Supplier")
                .contactEmail("duplicate@email.com")
                .country("USA")
                .build();

        when(supplierRepository.existsByContactEmailIgnoreCase("duplicate@email.com")).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> supplierService.createSupplier(requestDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(supplierRepository, never()).save(any());
    }


    @Test
    @DisplayName("Get supplier by existing ID - should return supplier")
    void getSupplierById_withExistingId_shouldReturnSupplierDTO() {
        when(supplierRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(supplier));
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.getSupplierById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Premium Furniture Co.", result.getName());
        verify(supplierRepository).findByIdWithChairs(1L);
    }

    @Test
    @DisplayName("Get supplier by non-existent ID - should throw exception")
    void getSupplierById_withNonExistentId_shouldThrowException() {
        when(supplierRepository.findByIdWithChairs(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> supplierService.getSupplierById(999L));

        assertEquals("Supplier not found with id: 999", exception.getMessage());
        verify(supplierRepository).findByIdWithChairs(999L);
    }

    @Test
    @DisplayName("Get supplier by email - should return supplier")
    void getSupplierByEmail_withExistingEmail_shouldReturnSupplierDTO() {
        when(supplierRepository.findByContactEmailIgnoreCase("contact@premiumfurniture.com"))
                .thenReturn(Optional.of(supplier));
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.getSupplierByEmail("contact@premiumfurniture.com");

        assertNotNull(result);
        assertEquals("contact@premiumfurniture.com", result.getContactEmail());
        verify(supplierRepository).findByContactEmailIgnoreCase("contact@premiumfurniture.com");
    }

    @Test
    @DisplayName("Get all suppliers - should return list")
    void getAllSuppliers_shouldReturnList() {
        List<Supplier> suppliers = Arrays.asList(supplier);
        when(supplierRepository.findAll()).thenReturn(suppliers);
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        List<SupplierDTO> result = supplierService.getAllSuppliers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Premium Furniture Co.", result.getFirst().getName());
        verify(supplierRepository).findAll();
    }

    @Test
    @DisplayName("Get suppliers by country - should return filtered list")
    void getSuppliersByCountry_shouldReturnFilteredList() {
        List<Supplier> suppliers = Arrays.asList(supplier);
        when(supplierRepository.findByCountryIgnoreCase("Germany")).thenReturn(suppliers);
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        List<SupplierDTO> result = supplierService.getSuppliersByCountry("Germany");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Germany", result.getFirst().getCountry());
        verify(supplierRepository).findByCountryIgnoreCase("Germany");
    }

    @Test
    @DisplayName("Update supplier with valid data - should succeed")
    void updateSupplier_withValidData_shouldReturnUpdatedSupplier() {
        SupplierDTO updateDTO = SupplierDTO.builder()
                .id(1L)
                .name("Updated Supplier")
                .contactEmail("updated@email.com")
                .country("France")
                .build();

        ChairDTO chairSimpleDTO = ChairDTO.builder()
                .id(1L)
                .name("Office Chair")
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByContactEmailIgnoreCaseAndIdNot("updated@email.com", 1L)).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);

        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(updateDTO);

        when(modelMapper.map(any(Chair.class), eq(ChairDTO.class)))
                .thenReturn(chairSimpleDTO);


        SupplierDTO result = supplierService.updateSupplier(updateDTO);

        assertNotNull(result);
        assertEquals("Updated Supplier", result.getName());
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Update supplier email to duplicate - should throw exception")
    void updateSupplier_withDuplicateEmail_shouldThrowException() {
        SupplierDTO updateDTO = SupplierDTO.builder()
                .id(1L)
                .name("Supplier")
                .contactEmail("duplicate@email.com")
                .country("USA")
                .build();

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByContactEmailIgnoreCaseAndIdNot("duplicate@email.com", 1L)).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> supplierService.updateSupplier(updateDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(supplierRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update supplier email only - should succeed")
    void updateSupplierEmail_shouldSucceed() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplierRepository.existsByContactEmailIgnoreCaseAndIdNot("new@email.com", 1L)).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.updateSupplierEmail(1L, "new@email.com");

        assertNotNull(result);
        verify(supplierRepository).save(any(Supplier.class));
    }


    @Test
    @DisplayName("Delete supplier without chairs - should succeed")
    void deleteSupplier_withoutChairs_shouldSucceed() {
        Supplier supplierWithoutChairs = Supplier.builder()
                .id(1L)
                .name("Supplier Without Chairs")
                .chairs(Arrays.asList())
                .build();

        when(supplierRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(supplierWithoutChairs));

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).delete(supplierWithoutChairs);
    }

    @Test
    @DisplayName("Delete supplier with chairs - should throw exception")
    void deleteSupplier_withChairs_shouldThrowException() {
        when(supplierRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(supplier));

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> supplierService.deleteSupplier(1L));

        assertTrue(exception.getMessage().contains("Cannot delete supplier with chair"));
        verify(supplierRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Force delete supplier - should succeed")
    void forceDeleteSupplier_shouldSucceed() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        supplierService.forceDeleteSupplier(1L);

        verify(supplierRepository).delete(supplier);
    }


    @Test
    @DisplayName("Add chair to supplier - should succeed")
    void addChairToSupplier_shouldSucceed() {
        Chair newChair = Chair.builder()
                .id(3L)
                .name("New Chair")
                .build();

        when(supplierRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(supplier));
        when(chairRepository.findById(3L)).thenReturn(Optional.of(newChair));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.addChairToSupplier(1L, 3L);

        assertNotNull(result);
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Add chair that already has different supplier - should throw exception")
    void addChairToSupplier_withChairAlreadyAssigned_shouldThrowException() {
        Supplier otherSupplier = Supplier.builder()
                .id(2L)
                .name("Other Supplier")
                .build();

        Chair chairWithSupplier = Chair.builder()
                .id(3L)
                .name("Chair with Supplier")
                .supplier(otherSupplier)
                .build();

        when(supplierRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(supplier));
        when(chairRepository.findById(3L)).thenReturn(Optional.of(chairWithSupplier));

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> supplierService.addChairToSupplier(1L, 3L));

        assertTrue(exception.getMessage().contains("already assigned to supplier"));
    }

    @Test
    @DisplayName("Remove chair from supplier - should succeed")
    void removeChairFromSupplier_shouldSucceed() {
        List<Chair> chairs = new java.util.ArrayList<>(Arrays.asList(chair1, chair2));
        Supplier supplierWithChairs = Supplier.builder()
                .id(1L)
                .name("Supplier")
                .chairs(chairs)
                .build();

        chair1.setSupplier(supplierWithChairs);

        when(supplierRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(supplierWithChairs));
        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair1));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplierWithChairs);
        when(modelMapper.map(supplierWithChairs, SupplierDTO.class)).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.removeChairFromSupplier(1L, 1L);

        assertNotNull(result);
        verify(supplierRepository).save(any(Supplier.class));
    }


    @Test
    @DisplayName("Search suppliers - should return matching suppliers")
    void searchSuppliers_shouldReturnMatching() {
        String keyword = "premium";
        List<Supplier> suppliers = Arrays.asList(supplier);
        when(supplierRepository.search(keyword)).thenReturn(suppliers);
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        List<SupplierDTO> result = supplierService.searchSuppliers(keyword, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supplierRepository).search(keyword);
    }

    @Test
    @DisplayName("Get chair count - should return correct count")
    void getChairCount_shouldReturnCount() {
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(supplierRepository.countChairsBySupplierId(1L)).thenReturn(2L);

        Long count = supplierService.getChairCount(1L);

        assertEquals(2L, count);
        verify(supplierRepository).countChairsBySupplierId(1L);
    }

    @Test
    @DisplayName("Get top suppliers - should return suppliers ordered by chair count")
    void getTopSuppliers_shouldReturnOrderedList() {
        Supplier topSupplier = Supplier.builder()
                .id(2L)
                .name("Top Supplier")
                .chairs(Arrays.asList(chair1, chair2, Chair.builder().id(3L).build()))
                .build();

        List<Supplier> suppliers = Arrays.asList(topSupplier, supplier);
        when(supplierRepository.findTopSuppliersByChairCount(5)).thenReturn(suppliers);
        when(modelMapper.map(any(Supplier.class), eq(SupplierDTO.class))).thenReturn(supplierDTO);

        List<SupplierDTO> result = supplierService.getTopSuppliers(5);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(supplierRepository).findTopSuppliersByChairCount(5);
    }

    @Test
    @DisplayName("Check if supplier exists by email - should return true")
    void existsByEmail_withExistingEmail_shouldReturnTrue() {
        when(supplierRepository.existsByContactEmailIgnoreCase("test@email.com")).thenReturn(true);

        boolean result = supplierService.existsByEmail("test@email.com");

        assertTrue(result);
        verify(supplierRepository).existsByContactEmailIgnoreCase("test@email.com");
    }
}