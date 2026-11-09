package etf.nwt.product.service;

import etf.nwt.product.dto.ChairVariantDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.model.*;
import etf.nwt.product.repository.ChairRepository;
import etf.nwt.product.repository.ChairVariantRepository;
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
@DisplayName("Chair Variant Service Tests")
class ChairVariantServiceTest {

    @Mock
    private ChairVariantRepository chairVariantRepository;

    @Mock
    private ChairRepository chairRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChairVariantService chairVariantService;

    private Chair chair;
    private ChairVariant variant;
    private ChairVariantDTO variantDTO;
    private Material material;
    private ColorOption colorOption;
    private Dimension dimension;

    @BeforeEach
    void setUp() {
        chair = Chair.builder()
                .id(1L)
                .name("Office Chair")
                .basePrice(new BigDecimal("199.99"))
                .build();

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
                .weightCapacity(120.0)
                .build();

        variant = ChairVariant.builder()
                .id(1L)
                .chair(chair)
                .material(material)
                .colorOption(colorOption)
                .dimension(dimension)
                .finalPrice(new BigDecimal("299.99"))
                .stockQuantity(10)
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
    @DisplayName("Create variant with valid data - should succeed")
    void createChairVariant_withValidData_shouldReturnVariantDTO() {
        ChairVariantDTO requestDTO = ChairVariantDTO.builder()
                .chairId(1L)
                .material(material)
                .colorOption(colorOption)
                .dimension(dimension)
                .finalPrice(new BigDecimal("299.99"))
                .stockQuantity(10)
                .build();

        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair));
        when(chairVariantRepository.existsByChairAndMaterialAndColorOptionAndDimension(
                chair, material, colorOption, dimension)).thenReturn(false);
        when(modelMapper.map(requestDTO, ChairVariant.class)).thenReturn(variant);
        when(chairVariantRepository.save(any(ChairVariant.class))).thenReturn(variant);
        when(modelMapper.map(variant, ChairVariantDTO.class)).thenReturn(variantDTO);

        ChairVariantDTO result = chairVariantService.createChairVariant(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("299.99"), result.getFinalPrice());
        verify(chairVariantRepository).save(any(ChairVariant.class));
    }

    @Test
    @DisplayName("Create variant with duplicate configuration - should throw exception")
    void createChairVariant_withDuplicateConfig_shouldThrowException() {
        ChairVariantDTO requestDTO = ChairVariantDTO.builder()
                .chairId(1L)
                .material(material)
                .colorOption(colorOption)
                .dimension(dimension)
                .build();

        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair));
        when(chairVariantRepository.existsByChairAndMaterialAndColorOptionAndDimension(
                chair, material, colorOption, dimension)).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> chairVariantService.createChairVariant(requestDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(chairVariantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create variant with price below base price - should throw exception")
    void createChairVariant_withPriceBelowBase_shouldThrowException() {
        ChairVariantDTO requestDTO = ChairVariantDTO.builder()
                .chairId(1L)
                .material(material)
                .colorOption(colorOption)
                .dimension(dimension)
                .finalPrice(new BigDecimal("50.00"))
                .build();

        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair));
        when(chairVariantRepository.existsByChairAndMaterialAndColorOptionAndDimension(
                chair, material, colorOption, dimension)).thenReturn(false);
        //when(modelMapper.map(requestDTO, ChairVariant.class)).thenReturn(variant);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> chairVariantService.createChairVariant(requestDTO));

        assertTrue(exception.getMessage().contains("cannot be less than base price"));
        verify(chairVariantRepository, never()).save(any());
    }


    @Test
    @DisplayName("Get variant by existing ID - should return variant")
    void getChairVariantById_withExistingId_shouldReturnVariantDTO() {
        when(chairVariantRepository.findByIdWithChair(1L)).thenReturn(Optional.of(variant));
        when(modelMapper.map(variant, ChairVariantDTO.class)).thenReturn(variantDTO);

        ChairVariantDTO result = chairVariantService.getChairVariantById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getChairId());
        verify(chairVariantRepository).findByIdWithChair(1L);
    }

    @Test
    @DisplayName("Get variants by chair ID - should return list")
    void getVariantsByChairId_shouldReturnList() {
        List<ChairVariant> variants = Arrays.asList(variant);
        when(chairVariantRepository.findByChairId(1L)).thenReturn(variants);
        when(modelMapper.map(variant, ChairVariantDTO.class)).thenReturn(variantDTO);

        List<ChairVariantDTO> result = chairVariantService.getVariantsByChairId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getChairId());
        verify(chairVariantRepository).findByChairId(1L);
    }

    @Test
    @DisplayName("Get in-stock variants - should return only variants with stock")
    void getInStockVariants_shouldReturnOnlyInStock() {
        ChairVariant outOfStock = ChairVariant.builder()
                .id(2L)
                .stockQuantity(0)
                .build();

        List<ChairVariant> variants = Arrays.asList(variant, outOfStock);
        when(chairVariantRepository.findByStockQuantityGreaterThan(0)).thenReturn(Arrays.asList(variant));
        when(modelMapper.map(variant, ChairVariantDTO.class)).thenReturn(variantDTO);

        List<ChairVariantDTO> result = chairVariantService.getInStockVariants();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.getFirst().getStockQuantity());
        verify(chairVariantRepository).findByStockQuantityGreaterThan(0);
    }


    @Test
    @DisplayName("Update variant with valid data - should succeed")
    void updateChairVariant_withValidData_shouldReturnUpdatedVariant() {
        ChairVariantDTO updateDTO = ChairVariantDTO.builder()
                .id(1L)
                .chairId(1L)
                .material(material)
                .colorOption(ColorOption.builder().id(2L).name("Red").build())
                .dimension(dimension)
                .finalPrice(new BigDecimal("349.99"))
                .stockQuantity(20)
                .build();

        when(chairVariantRepository.findById(1L)).thenReturn(Optional.of(variant));
        //when(chairRepository.findById(1L)).thenReturn(Optional.of(chair));
        when(chairVariantRepository.existsByChairAndMaterialAndColorOptionAndDimensionAndIdNot(
                chair, material, updateDTO.getColorOption(), dimension, 1L)).thenReturn(false);
        when(chairVariantRepository.save(any(ChairVariant.class))).thenReturn(variant);
        when(modelMapper.map(variant, ChairVariantDTO.class)).thenReturn(updateDTO);

        //doNothing().when(modelMapper).map(any(ChairVariantDTO.class), any(ChairVariant.class));

        ChairVariantDTO result = chairVariantService.updateChairVariant(updateDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("349.99"), result.getFinalPrice());
        verify(chairVariantRepository).save(any(ChairVariant.class));
    }

    @Test
    @DisplayName("Update variant stock quantity - should succeed")
    void updateStockQuantity_shouldUpdateStock() {
        when(chairVariantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(chairVariantRepository.save(any(ChairVariant.class))).thenReturn(variant);
        when(modelMapper.map(variant, ChairVariantDTO.class)).thenReturn(variantDTO);

        ChairVariantDTO result = chairVariantService.updateStockQuantity(1L, 50);

        assertNotNull(result);
        verify(chairVariantRepository).save(any(ChairVariant.class));
    }


    @Test
    @DisplayName("Delete existing variant - should succeed")
    void deleteChairVariant_withExistingId_shouldSucceed() {
        when(chairVariantRepository.existsById(1L)).thenReturn(true);

        chairVariantService.deleteChairVariant(1L);

        verify(chairVariantRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete variants by chair ID - should succeed")
    void deleteVariantsByChairId_shouldSucceed() {
        when(chairRepository.existsById(1L)).thenReturn(true);

        chairVariantService.deleteVariantsByChairId(1L);

        verify(chairVariantRepository).deleteByChairId(1L);
    }


    @Test
    @DisplayName("Check stock availability - sufficient stock should return true")
    void checkStockAvailability_withSufficientStock_shouldReturnTrue() {
        when(chairVariantRepository.findById(1L)).thenReturn(Optional.of(variant));

        boolean result = chairVariantService.checkStockAvailability(1L, 5);

        assertTrue(result);
    }

    @Test
    @DisplayName("Check stock availability - insufficient stock should return false")
    void checkStockAvailability_withInsufficientStock_shouldReturnFalse() {
        when(chairVariantRepository.findById(1L)).thenReturn(Optional.of(variant));

        boolean result = chairVariantService.checkStockAvailability(1L, 15);

        assertFalse(result);
    }

    @Test
    @DisplayName("Reduce stock - should decrease stock quantity")
    void reduceStock_shouldDecreaseQuantity() {
        when(chairVariantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(chairVariantRepository.save(any(ChairVariant.class))).thenReturn(variant);

        chairVariantService.reduceStock(1L, 5);

        verify(chairVariantRepository).save(any(ChairVariant.class));
    }

    @Test
    @DisplayName("Reduce stock with insufficient quantity - should throw exception")
    void reduceStock_withInsufficientQuantity_shouldThrowException() {
        when(chairVariantRepository.findById(1L)).thenReturn(Optional.of(variant));

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> chairVariantService.reduceStock(1L, 15));

        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    @DisplayName("Increase stock - should increase stock quantity")
    void increaseStock_shouldIncreaseQuantity() {
        when(chairVariantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(chairVariantRepository.save(any(ChairVariant.class))).thenReturn(variant);

        chairVariantService.increaseStock(1L, 10);

        verify(chairVariantRepository).save(any(ChairVariant.class));
    }


    @Test
    @DisplayName("Validate final price not less than base price - should succeed")
    void validatePrice_notLessThanBase_shouldSucceed() {
        variant.setFinalPrice(new BigDecimal("250.00"));

        assertTrue(variant.getFinalPrice().compareTo(chair.getBasePrice()) >= 0);
    }
}