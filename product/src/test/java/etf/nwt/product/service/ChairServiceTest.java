package etf.nwt.product.service;

import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.dto.CategoryDTO;
import etf.nwt.product.dto.SupplierDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.*;
import etf.nwt.product.repository.ChairRepository;
import etf.nwt.product.repository.CategoryRepository;
import etf.nwt.product.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Chair Service Tests")
class ChairServiceTest {

    @Mock
    private ChairRepository chairRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChairService chairService;

    private ChairDTO chairDTO;
    private Chair chair;
    private Category category;
    private Supplier supplier;
    private ChairVariant variant1;
    private ChairVariant variant2;

    @BeforeEach
    void setUp() {

        category = Category.builder()
                .id(1L)
                .name("Office Chairs")
                .build();

        supplier = Supplier.builder()
                .id(1L)
                .name("Premium Furniture Co.")
                .country("Germany")
                .build();

        variant1 = ChairVariant.builder()
                .id(1L)
                .material(Material.builder().id(1L).name("Leather").build())
                .colorOption(ColorOption.builder().id(1L).name("Black").build())
                .dimension(Dimension.builder().id(1L).name("Medium").build())
                .finalPrice(new BigDecimal("299.99"))
                .stockQuantity(10)
                .build();

        variant2 = ChairVariant.builder()
                .id(2L)
                .material(Material.builder().id(2L).name("Fabric").build())
                .colorOption(ColorOption.builder().id(2L).name("Gray").build())
                .dimension(Dimension.builder().id(2L).name("Large").build())
                .finalPrice(new BigDecimal("349.99"))
                .stockQuantity(5)
                .build();

        chair = Chair.builder()
                .id(1L)
                .name("Ergonomic Office Chair")
                .description("Comfortable office chair")
                .basePrice(new BigDecimal("199.99"))
                .category(category)
                .supplier(supplier)
                .variants(Arrays.asList(variant1, variant2))
                .build();

        chairDTO = ChairDTO.builder()
                .id(1L)
                .name("Ergonomic Office Chair")
                .description("Comfortable office chair")
                .basePrice(new BigDecimal("199.99"))
                .categoryId(1L)
                .supplierId(1L)
                .category(CategoryDTO.builder()
                        .id(1L)
                        .name("Office Chairs")
                        .build())
                .supplier(SupplierDTO.builder()
                        .id(1L)
                        .name("Premium Furniture Co.")
                        .country("Germany")
                        .build())
                .build();
    }

    @Test
    @DisplayName("Create chair with valid data - should succeed")
    void createChair_withValidData_shouldReturnChairDTO() {
        ChairDTO requestDTO = ChairDTO.builder()
                .name("New Office Chair")
                .basePrice(new BigDecimal("299.99"))
                .categoryId(1L)
                .supplierId(1L)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(modelMapper.map(requestDTO, Chair.class)).thenReturn(chair);
        when(chairRepository.save(any(Chair.class))).thenReturn(chair);
        when(modelMapper.map(chair, ChairDTO.class)).thenReturn(chairDTO);

        ChairDTO result = chairService.createChair(requestDTO);

        assertNotNull(result);
        assertEquals("Ergonomic Office Chair", result.getName());
        assertEquals(new BigDecimal("199.99"), result.getBasePrice());
        assertEquals(1L, result.getCategoryId());
        assertEquals(1L, result.getSupplierId());

        verify(categoryRepository).findById(1L);
        verify(supplierRepository).findById(1L);
        verify(chairRepository).save(any(Chair.class));
        verify(modelMapper).map(requestDTO, Chair.class);
        verify(modelMapper).map(chair, ChairDTO.class);
    }

    @Test
    @DisplayName("Create chair with non-existent category - should throw exception")
    void createChair_withNonExistentCategory_shouldThrowException() {
        ChairDTO requestDTO = ChairDTO.builder()
                .name("New Office Chair")
                .basePrice(new BigDecimal("299.99"))
                .categoryId(999L)
                .supplierId(1L)
                .build();

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> chairService.createChair(requestDTO));

        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryRepository).findById(999L);
        verify(supplierRepository, never()).findById(any());
        verify(chairRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create chair with non-existent supplier - should throw exception")
    void createChair_withNonExistentSupplier_shouldThrowException() {
        ChairDTO requestDTO = ChairDTO.builder()
                .name("New Office Chair")
                .basePrice(new BigDecimal("299.99"))
                .categoryId(1L)
                .supplierId(999L)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> chairService.createChair(requestDTO));

        assertEquals("Supplier not found with id: 999", exception.getMessage());
        verify(categoryRepository).findById(1L);
        verify(supplierRepository).findById(999L);
        verify(chairRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get chair by existing ID - should return chair")
    void getChairById_withExistingId_shouldReturnChairDTO() {
        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair));
        when(modelMapper.map(chair, ChairDTO.class)).thenReturn(chairDTO);

        ChairDTO result = chairService.getChairById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ergonomic Office Chair", result.getName());
        verify(chairRepository).findById(1L);
    }

    @Test
    @DisplayName("Get chair by non-existent ID - should throw exception")
    void getChairById_withNonExistentId_shouldThrowException() {
        when(chairRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> chairService.getChairById(999L));

        assertEquals("Chair not found with id: 999", exception.getMessage());
        verify(chairRepository).findById(999L);
    }

    @Test
    @DisplayName("Get all chairs - should return list")
    void getAllChairs_shouldReturnList() {
        List<Chair> chairs = Arrays.asList(chair);
        when(chairRepository.findAll()).thenReturn(chairs);
        when(modelMapper.map(chair, ChairDTO.class)).thenReturn(chairDTO);

        List<ChairDTO> result = chairService.getAllChairs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ergonomic Office Chair", result.getFirst().getName());
        verify(chairRepository).findAll();
    }

    @Test
    @DisplayName("Get all chairs when empty - should return empty list")
    void getAllChairs_whenNoChairs_shouldReturnEmptyList() {
        when(chairRepository.findAll()).thenReturn(Arrays.asList());

        List<ChairDTO> result = chairService.getAllChairs();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(chairRepository).findAll();
    }


    @Test
    @DisplayName("Update chair with valid data - should succeed")
    void updateChair_withValidData_shouldReturnUpdatedChair() {
        ChairDTO updateDTO = ChairDTO.builder()
                .id(1L)
                .name("Updated Office Chair")
                .description("Updated description")
                .basePrice(new BigDecimal("249.99"))
                .categoryId(2L)
                .supplierId(2L)
                .build();

        Category newCategory = Category.builder().id(2L).name("New Category").build();
        Supplier newSupplier = Supplier.builder().id(2L).name("New Supplier").build();

        Chair updatedChair = Chair.builder()
                .id(1L)
                .name("Updated Office Chair")
                .description("Updated description")
                .basePrice(new BigDecimal("249.99"))
                .category(newCategory)
                .supplier(newSupplier)
                .build();

        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(supplierRepository.findById(2L)).thenReturn(Optional.of(newSupplier));
        when(chairRepository.save(any(Chair.class))).thenReturn(chair);
        when(modelMapper.map(chair, ChairDTO.class)).thenReturn(updateDTO);

        doNothing().when(modelMapper).map(any(ChairDTO.class), any(Chair.class));

        when(modelMapper.map(chair, ChairDTO.class)).thenReturn(updateDTO);
        ChairDTO result = chairService.updateChair(updateDTO);

        assertNotNull(result);
        assertEquals("Updated Office Chair", result.getName());
        verify(chairRepository).findById(1L);
        verify(categoryRepository).findById(2L);
        verify(supplierRepository).findById(2L);
        verify(chairRepository).save(any(Chair.class));
    }

    @Test
    @DisplayName("Update non-existent chair - should throw exception")
    void updateChair_withNonExistentId_shouldThrowException() {
        ChairDTO updateDTO = ChairDTO.builder()
                .id(999L)
                .name("Updated Chair")
                .build();

        when(chairRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> chairService.updateChair(updateDTO));

        assertEquals("Chair with ID 999 not found", exception.getMessage());
        verify(chairRepository).findById(999L);
        verify(chairRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update chair with non-existent category - should throw exception")
    void updateChair_withNonExistentCategory_shouldThrowException() {
        ChairDTO updateDTO = ChairDTO.builder()
                .id(1L)
                .name("Updated Chair")
                .categoryId(999L)
                .build();

        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> chairService.updateChair(updateDTO));

        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(chairRepository).findById(1L);
        verify(categoryRepository).findById(999L);
        verify(chairRepository, never()).save(any());
    }


    @Test
    @DisplayName("Delete existing chair - should succeed")
    void deleteChair_withExistingId_shouldSucceed() {
        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair));
        doNothing().when(chairRepository).deleteById(1L);

        chairService.deleteChair(1L);

        verify(chairRepository).findById(1L);
        verify(chairRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete non-existent chair - should throw exception")
    void deleteChair_withNonExistentId_shouldThrowException() {
        when(chairRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> chairService.deleteChair(999L));

        assertEquals("Chair with ID 999 not found", exception.getMessage());
        verify(chairRepository).findById(999L);
        verify(chairRepository, never()).deleteById(any());
    }

}