package etf.nwt.product.service;

import etf.nwt.product.dto.DimensionDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.Dimension;
import etf.nwt.product.repository.DimensionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DimensionServiceTest {

    @Mock
    private DimensionRepository dimensionRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DimensionService dimensionService;

    private Dimension dimension;
    private DimensionDTO dimensionDTO;
    private final Long DIMENSION_ID = 1L;
    private final String DIMENSION_NAME = "Standard Box";

    @BeforeEach
    void setUp() {
        dimension = Dimension.builder()
                .id(DIMENSION_ID)
                .name(DIMENSION_NAME)
                .width(30.0)
                .height(20.0)
                .depth(15.0)
                .weightCapacity(25.0)
                .build();

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
    void createDimension_Success() {
        when(dimensionRepository.existsByName(DIMENSION_NAME)).thenReturn(false);
        when(modelMapper.map(dimensionDTO, Dimension.class)).thenReturn(dimension);
        when(dimensionRepository.save(dimension)).thenReturn(dimension);
        when(modelMapper.map(dimension, DimensionDTO.class)).thenReturn(dimensionDTO);

        DimensionDTO result = dimensionService.createDimension(dimensionDTO);

        assertNotNull(result);
        assertEquals(DIMENSION_ID, result.getId());
        assertEquals(DIMENSION_NAME, result.getName());
        assertEquals(30.0, result.getWidth());
        assertEquals(20.0, result.getHeight());
        assertEquals(15.0, result.getDepth());
        assertEquals(25.0, result.getWeightCapacity());

        verify(dimensionRepository).existsByName(DIMENSION_NAME);
        verify(dimensionRepository).save(dimension);
    }

    @Test
    void createDimension_WhenNameExists_ThrowsException() {
        when(dimensionRepository.existsByName(DIMENSION_NAME)).thenReturn(true);

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> dimensionService.createDimension(dimensionDTO)
        );

        assertEquals("Dimension with name '" + DIMENSION_NAME + "' already exists", exception.getMessage());
        verify(dimensionRepository, never()).save(any(Dimension.class));
    }

    @Test
    void getDimensionById_Success() {
        when(dimensionRepository.findById(DIMENSION_ID)).thenReturn(Optional.of(dimension));
        when(modelMapper.map(dimension, DimensionDTO.class)).thenReturn(dimensionDTO);

        DimensionDTO result = dimensionService.getDimensionById(DIMENSION_ID);

        assertNotNull(result);
        assertEquals(DIMENSION_ID, result.getId());
        assertEquals(DIMENSION_NAME, result.getName());
        verify(dimensionRepository).findById(DIMENSION_ID);
    }

    @Test
    void getDimensionById_WhenNotFound_ThrowsException() {
        when(dimensionRepository.findById(DIMENSION_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dimensionService.getDimensionById(DIMENSION_ID)
        );

        assertEquals("Dimension not found with id: " + DIMENSION_ID, exception.getMessage());
        verify(dimensionRepository).findById(DIMENSION_ID);
    }

    @Test
    void getDimensionByName_Success() {
        when(dimensionRepository.findByName(DIMENSION_NAME)).thenReturn(Optional.of(dimension));
        when(modelMapper.map(dimension, DimensionDTO.class)).thenReturn(dimensionDTO);

        DimensionDTO result = dimensionService.getDimensionByName(DIMENSION_NAME);

        assertNotNull(result);
        assertEquals(DIMENSION_NAME, result.getName());
        verify(dimensionRepository).findByName(DIMENSION_NAME);
    }

    @Test
    void getDimensionByName_WhenNotFound_ThrowsException() {
        when(dimensionRepository.findByName(DIMENSION_NAME)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dimensionService.getDimensionByName(DIMENSION_NAME)
        );

        assertEquals("Dimension not found with name: " + DIMENSION_NAME, exception.getMessage());
        verify(dimensionRepository).findByName(DIMENSION_NAME);
    }

    @Test
    void getAllDimensions_Success() {
        List<Dimension> dimensions = Arrays.asList(dimension);
        when(dimensionRepository.findAll()).thenReturn(dimensions);
        when(modelMapper.map(dimension, DimensionDTO.class)).thenReturn(dimensionDTO);

        List<DimensionDTO> results = dimensionService.getAllDimensions();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(DIMENSION_ID, results.getFirst().getId());
        verify(dimensionRepository).findAll();
    }

    @Test
    void getAllDimensions_WhenEmpty_ReturnsEmptyList() {
        when(dimensionRepository.findAll()).thenReturn(Arrays.asList());

        List<DimensionDTO> results = dimensionService.getAllDimensions();

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(dimensionRepository).findAll();
    }

    @Test
    void searchDimensions_Success() {
        String keyword = "box";
        List<Dimension> dimensions = Arrays.asList(dimension);
        when(dimensionRepository.search(keyword)).thenReturn(dimensions);
        when(modelMapper.map(dimension, DimensionDTO.class)).thenReturn(dimensionDTO);

        List<DimensionDTO> results = dimensionService.searchDimensions(keyword);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(DIMENSION_NAME, results.getFirst().getName());
        verify(dimensionRepository).search(keyword);
    }

    @Test
    void getDimensionsBySizeRange_Success() {
        Double minWidth = 20.0;
        Double maxWidth = 40.0;
        Double minHeight = 15.0;
        Double maxHeight = 25.0;
        Double minDepth = 10.0;
        Double maxDepth = 20.0;

        List<Dimension> dimensions = Arrays.asList(dimension);
        when(dimensionRepository.findBySizeRange(
                minWidth, maxWidth,
                minHeight, maxHeight,
                minDepth, maxDepth)).thenReturn(dimensions);
        when(modelMapper.map(dimension, DimensionDTO.class)).thenReturn(dimensionDTO);

        List<DimensionDTO> results = dimensionService.getDimensionsBySizeRange(
                minWidth, maxWidth,
                minHeight, maxHeight,
                minDepth, maxDepth);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(dimensionRepository).findBySizeRange(
                minWidth, maxWidth,
                minHeight, maxHeight,
                minDepth, maxDepth);
    }

    @Test
    void getDimensionsByMinWeightCapacity_Success() {
        Double minWeightCapacity = 20.0;
        List<Dimension> dimensions = Arrays.asList(dimension);
        when(dimensionRepository.findByMinWeightCapacity(minWeightCapacity)).thenReturn(dimensions);
        when(modelMapper.map(dimension, DimensionDTO.class)).thenReturn(dimensionDTO);

        List<DimensionDTO> results = dimensionService.getDimensionsByMinWeightCapacity(minWeightCapacity);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(dimensionRepository).findByMinWeightCapacity(minWeightCapacity);
    }

    @Test
    void getSizeStatistics_Success() {
        Object[] statistics = new Object[]{10.0, 50.0, 30.0, 5.0, 30.0, 15.0, 2.0, 20.0, 10.0};
        when(dimensionRepository.getSizeStatistics()).thenReturn(statistics);

        Object result = dimensionService.getSizeStatistics();

        assertNotNull(result);
        assertArrayEquals(statistics, (Object[]) result);
        verify(dimensionRepository).getSizeStatistics();
    }

    @Test
    void updateDimension_Success() {
        DimensionDTO updatedDTO = DimensionDTO.builder()
                .name("Updated Box")
                .width(35.0)
                .height(25.0)
                .depth(20.0)
                .weightCapacity(30.0)
                .build();

        when(dimensionRepository.findById(DIMENSION_ID)).thenReturn(Optional.of(dimension));
        when(dimensionRepository.existsByNameAndIdNot("Updated Box", DIMENSION_ID)).thenReturn(false);
        when(dimensionRepository.save(any(Dimension.class))).thenReturn(dimension);
        when(modelMapper.map(dimension, DimensionDTO.class)).thenReturn(updatedDTO);

        DimensionDTO result = dimensionService.updateDimension(DIMENSION_ID, updatedDTO);

        assertNotNull(result);
        assertEquals("Updated Box", result.getName());
        assertEquals(35.0, result.getWidth());
        assertEquals(25.0, result.getHeight());
        assertEquals(20.0, result.getDepth());
        assertEquals(30.0, result.getWeightCapacity());

        verify(dimensionRepository).findById(DIMENSION_ID);
        verify(dimensionRepository).existsByNameAndIdNot("Updated Box", DIMENSION_ID);
        verify(dimensionRepository).save(dimension);
    }

    @Test
    void updateDimension_WhenNotFound_ThrowsException() {
        when(dimensionRepository.findById(DIMENSION_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dimensionService.updateDimension(DIMENSION_ID, dimensionDTO)
        );

        assertEquals("Dimension not found with id: " + DIMENSION_ID, exception.getMessage());
        verify(dimensionRepository, never()).save(any(Dimension.class));
    }

    @Test
    void updateDimension_WhenNameConflict_ThrowsException() {
        DimensionDTO updatedDTO = DimensionDTO.builder()
                .name("Existing Box")
                .build();

        when(dimensionRepository.findById(DIMENSION_ID)).thenReturn(Optional.of(dimension));
        when(dimensionRepository.existsByNameAndIdNot("Existing Box", DIMENSION_ID)).thenReturn(true);

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> dimensionService.updateDimension(DIMENSION_ID, updatedDTO)
        );

        assertEquals("Dimension with name 'Existing Box' already exists", exception.getMessage());
        verify(dimensionRepository, never()).save(any(Dimension.class));
    }

    @Test
    void deleteDimension_Success() {
        when(dimensionRepository.existsById(DIMENSION_ID)).thenReturn(true);
        doNothing().when(dimensionRepository).deleteById(DIMENSION_ID);

        dimensionService.deleteDimension(DIMENSION_ID);

        verify(dimensionRepository).existsById(DIMENSION_ID);
        verify(dimensionRepository).deleteById(DIMENSION_ID);
    }

    @Test
    void deleteDimension_WhenNotFound_ThrowsException() {
        when(dimensionRepository.existsById(DIMENSION_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dimensionService.deleteDimension(DIMENSION_ID)
        );

        assertEquals("Dimension not found with id: " + DIMENSION_ID, exception.getMessage());
        verify(dimensionRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsById_Success() {
        when(dimensionRepository.existsById(DIMENSION_ID)).thenReturn(true);

        boolean exists = dimensionService.existsById(DIMENSION_ID);

        assertTrue(exists);
        verify(dimensionRepository).existsById(DIMENSION_ID);
    }

    @Test
    void existsById_WhenNotExists_ReturnsFalse() {
        when(dimensionRepository.existsById(DIMENSION_ID)).thenReturn(false);

        boolean exists = dimensionService.existsById(DIMENSION_ID);

        assertFalse(exists);
        verify(dimensionRepository).existsById(DIMENSION_ID);
    }
}