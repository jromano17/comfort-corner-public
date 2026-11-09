package etf.nwt.product.service;

import etf.nwt.product.dto.ColorOptionDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.ColorOption;
import etf.nwt.product.repository.ColorOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Color Option Service Tests")
class ColorOptionServiceTest {

    @Mock
    private ColorOptionRepository colorOptionRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ColorOptionService colorOptionService;

    private ColorOption colorOption;
    private ColorOptionDTO colorOptionDTO;

    @BeforeEach
    void setUp() {
        colorOption = ColorOption.builder()
                .id(1L)
                .name("Red")
                .hexCode("#FF0000")
                .build();

        colorOptionDTO = ColorOptionDTO.builder()
                .id(1L)
                .name("Red")
                .hexCode("#FF0000")
                .build();
    }


    @Test
    @DisplayName("Create color option with valid data - should succeed")
    void createColorOption_withValidData_shouldReturnColorOptionDTO() {
        ColorOptionDTO requestDTO = ColorOptionDTO.builder()
                .name("Blue")
                .hexCode("#0000FF")
                .build();

        when(colorOptionRepository.existsByName("Blue")).thenReturn(false);
        when(colorOptionRepository.existsByHexCode("#0000FF")).thenReturn(false);
        when(modelMapper.map(requestDTO, ColorOption.class)).thenReturn(colorOption);
        when(colorOptionRepository.save(any(ColorOption.class))).thenReturn(colorOption);
        when(modelMapper.map(colorOption, ColorOptionDTO.class)).thenReturn(colorOptionDTO);

        ColorOptionDTO result = colorOptionService.createColorOption(requestDTO);

        assertNotNull(result);
        assertEquals("Red", result.getName());
        assertEquals("#FF0000", result.getHexCode());
        verify(colorOptionRepository).save(any(ColorOption.class));
    }

    @Test
    @DisplayName("Create color option with duplicate name - should throw exception")
    void createColorOption_withDuplicateName_shouldThrowException() {
        ColorOptionDTO requestDTO = ColorOptionDTO.builder()
                .name("Duplicate Red")
                .hexCode("#FF0000")
                .build();

        when(colorOptionRepository.existsByName("Duplicate Red")).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> colorOptionService.createColorOption(requestDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(colorOptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create color option with duplicate hex code - should throw exception")
    void createColorOption_withDuplicateHexCode_shouldThrowException() {
        ColorOptionDTO requestDTO = ColorOptionDTO.builder()
                .name("New Red")
                .hexCode("#FF0000")
                .build();

        when(colorOptionRepository.existsByName("New Red")).thenReturn(false);
        when(colorOptionRepository.existsByHexCode("#FF0000")).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> colorOptionService.createColorOption(requestDTO));

        assertTrue(exception.getMessage().contains("hex code"));
        verify(colorOptionRepository, never()).save(any());
    }


    @Test
    @DisplayName("Get color option by existing ID - should return color option")
    void getColorOptionById_withExistingId_shouldReturnColorOptionDTO() {
        when(colorOptionRepository.findById(1L)).thenReturn(Optional.of(colorOption));
        when(modelMapper.map(colorOption, ColorOptionDTO.class)).thenReturn(colorOptionDTO);

        ColorOptionDTO result = colorOptionService.getColorOptionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Red", result.getName());
        verify(colorOptionRepository).findById(1L);
    }

    @Test
    @DisplayName("Get color option by non-existent ID - should throw exception")
    void getColorOptionById_withNonExistentId_shouldThrowException() {
        when(colorOptionRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> colorOptionService.getColorOptionById(999L));

        assertEquals("Color option not found with id: 999", exception.getMessage());
        verify(colorOptionRepository).findById(999L);
    }

    @Test
    @DisplayName("Get color option by name - should return color option")
    void getColorOptionByName_withExistingName_shouldReturnColorOptionDTO() {
        when(colorOptionRepository.findByName("Red")).thenReturn(Optional.of(colorOption));
        when(modelMapper.map(colorOption, ColorOptionDTO.class)).thenReturn(colorOptionDTO);

        ColorOptionDTO result = colorOptionService.getColorOptionByName("Red");

        assertNotNull(result);
        assertEquals("Red", result.getName());
        verify(colorOptionRepository).findByName("Red");
    }

    @Test
    @DisplayName("Get color option by hex code - should return color option")
    void getColorOptionByHexCode_withExistingHex_shouldReturnColorOptionDTO() {
        when(colorOptionRepository.findByHexCode("#FF0000")).thenReturn(Optional.of(colorOption));
        when(modelMapper.map(colorOption, ColorOptionDTO.class)).thenReturn(colorOptionDTO);

        ColorOptionDTO result = colorOptionService.getColorOptionByHexCode("#FF0000");

        assertNotNull(result);
        assertEquals("#FF0000", result.getHexCode());
        verify(colorOptionRepository).findByHexCode("#FF0000");
    }

    @Test
    @DisplayName("Get all color options - should return list")
    void getAllColorOptions_shouldReturnList() {
        List<ColorOption> colorOptions = Arrays.asList(colorOption);
        when(colorOptionRepository.findAll()).thenReturn(colorOptions);
        when(modelMapper.map(colorOption, ColorOptionDTO.class)).thenReturn(colorOptionDTO);

        List<ColorOptionDTO> result = colorOptionService.getAllColorOptions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Red", result.getFirst().getName());
        verify(colorOptionRepository).findAll();
    }


    @Test
    @DisplayName("Search color options - should return matching results")
    void searchColorOptions_shouldReturnMatching() {
        String keyword = "red";
        List<ColorOption> colorOptions = Arrays.asList(colorOption);
        when(colorOptionRepository.search(keyword)).thenReturn(colorOptions);
        when(modelMapper.map(colorOption, ColorOptionDTO.class)).thenReturn(colorOptionDTO);

        List<ColorOptionDTO> result = colorOptionService.searchColorOptions(keyword);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(colorOptionRepository).search(keyword);
    }


    @Test
    @DisplayName("Update color option with valid data - should succeed")
    void updateColorOption_withValidData_shouldReturnUpdatedColorOption() {
        ColorOptionDTO updateDTO = ColorOptionDTO.builder()
                .id(1L)
                .name("Updated Red")
                .hexCode("#FF1111")
                .build();

        when(colorOptionRepository.findById(1L)).thenReturn(Optional.of(colorOption));
        when(colorOptionRepository.existsByNameAndIdNot("Updated Red", 1L)).thenReturn(false);
        when(colorOptionRepository.existsByHexCodeAndIdNot("#FF1111", 1L)).thenReturn(false);
        when(colorOptionRepository.save(any(ColorOption.class))).thenReturn(colorOption);
        when(modelMapper.map(colorOption, ColorOptionDTO.class)).thenReturn(updateDTO);

        //doNothing().when(modelMapper).map(any(ColorOptionDTO.class), any(ColorOption.class));

        ColorOptionDTO result = colorOptionService.updateColorOption(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Updated Red", result.getName());
        verify(colorOptionRepository).save(any(ColorOption.class));
    }

    @Test
    @DisplayName("Update color option with duplicate name - should throw exception")
    void updateColorOption_withDuplicateName_shouldThrowException() {
        ColorOptionDTO updateDTO = ColorOptionDTO.builder()
                .id(1L)
                .name("Duplicate Name")
                .hexCode("#FF0000")
                .build();

        when(colorOptionRepository.findById(1L)).thenReturn(Optional.of(colorOption));
        when(colorOptionRepository.existsByNameAndIdNot("Duplicate Name", 1L)).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> colorOptionService.updateColorOption(1L, updateDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(colorOptionRepository, never()).save(any());
    }


    @Test
    @DisplayName("Delete existing color option - should succeed")
    void deleteColorOption_withExistingId_shouldSucceed() {
        when(colorOptionRepository.existsById(1L)).thenReturn(true);

        colorOptionService.deleteColorOption(1L);

        verify(colorOptionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete non-existent color option - should throw exception")
    void deleteColorOption_withNonExistentId_shouldThrowException() {
        when(colorOptionRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> colorOptionService.deleteColorOption(999L));

        assertEquals("Color option not found with id: 999", exception.getMessage());
        verify(colorOptionRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("Check if color option exists by name - should return true")
    void existsByName_withExistingName_shouldReturnTrue() {
        when(colorOptionRepository.existsByName("Red")).thenReturn(true);

        boolean result = colorOptionService.existsByName("Red");

        assertTrue(result);
        verify(colorOptionRepository).existsByName("Red");
    }

    @Test
    @DisplayName("Check if color option exists by hex code - should return true")
    void existsByHexCode_withExistingHex_shouldReturnTrue() {
        when(colorOptionRepository.existsByHexCode("#FF0000")).thenReturn(true);

        boolean result = colorOptionService.existsByHexCode("#FF0000");

        assertTrue(result);
        verify(colorOptionRepository).existsByHexCode("#FF0000");
    }
}