package etf.nwt.product.service;

import etf.nwt.product.dto.MaterialDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.Material;
import etf.nwt.product.repository.MaterialRepository;
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
@DisplayName("Material Service Tests")
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MaterialService materialService;

    private Material material;
    private MaterialDTO materialDTO;

    @BeforeEach
    void setUp() {
        material = Material.builder()
                .id(1L)
                .name("Leather")
                .description("Premium leather material")
                .build();

        materialDTO = MaterialDTO.builder()
                .id(1L)
                .name("Leather")
                .description("Premium leather material")
                .build();
    }


    @Test
    @DisplayName("Create material with valid data - should succeed")
    void createMaterial_withValidData_shouldReturnMaterialDTO() {
        MaterialDTO requestDTO = MaterialDTO.builder()
                .name("New Material")
                .description("New material description")
                .build();

        when(materialRepository.existsByName("New Material")).thenReturn(false);
        when(modelMapper.map(requestDTO, Material.class)).thenReturn(material);
        when(materialRepository.save(any(Material.class))).thenReturn(material);
        when(modelMapper.map(material, MaterialDTO.class)).thenReturn(materialDTO);

        MaterialDTO result = materialService.createMaterial(requestDTO);

        assertNotNull(result);
        assertEquals("Leather", result.getName());
        verify(materialRepository).save(any(Material.class));
    }

    @Test
    @DisplayName("Create material with duplicate name - should throw exception")
    void createMaterial_withDuplicateName_shouldThrowException() {
        MaterialDTO requestDTO = MaterialDTO.builder()
                .name("Duplicate Material")
                .build();

        when(materialRepository.existsByName("Duplicate Material")).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> materialService.createMaterial(requestDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(materialRepository, never()).save(any());
    }


    @Test
    @DisplayName("Get material by existing ID - should return material")
    void getMaterialById_withExistingId_shouldReturnMaterialDTO() {
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(modelMapper.map(material, MaterialDTO.class)).thenReturn(materialDTO);

        MaterialDTO result = materialService.getMaterialById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Leather", result.getName());
        verify(materialRepository).findById(1L);
    }

    @Test
    @DisplayName("Get material by non-existent ID - should throw exception")
    void getMaterialById_withNonExistentId_shouldThrowException() {
        when(materialRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> materialService.getMaterialById(999L));

        assertEquals("Material not found with id: 999", exception.getMessage());
        verify(materialRepository).findById(999L);
    }

    @Test
    @DisplayName("Get material by name - should return material")
    void getMaterialByName_withExistingName_shouldReturnMaterialDTO() {
        when(materialRepository.findByName("Leather")).thenReturn(Optional.of(material));
        when(modelMapper.map(material, MaterialDTO.class)).thenReturn(materialDTO);

        MaterialDTO result = materialService.getMaterialByName("Leather");

        assertNotNull(result);
        assertEquals("Leather", result.getName());
        verify(materialRepository).findByName("Leather");
    }

    @Test
    @DisplayName("Get all materials - should return list")
    void getAllMaterials_shouldReturnList() {
        List<Material> materials = Arrays.asList(material);
        when(materialRepository.findAll()).thenReturn(materials);
        when(modelMapper.map(material, MaterialDTO.class)).thenReturn(materialDTO);

        List<MaterialDTO> result = materialService.getAllMaterials();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Leather", result.getFirst().getName());
        verify(materialRepository).findAll();
    }

    @Test
    @DisplayName("Search materials - should return matching materials")
    void searchMaterials_shouldReturnMatching() {
        String keyword = "leather";
        List<Material> materials = Arrays.asList(material);
        when(materialRepository.search(keyword)).thenReturn(materials);
        when(modelMapper.map(material, MaterialDTO.class)).thenReturn(materialDTO);

        List<MaterialDTO> result = materialService.searchMaterials(keyword);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(materialRepository).search(keyword);
    }


    @Test
    @DisplayName("Update material with valid data - should succeed")
    void updateMaterial_withValidData_shouldReturnUpdatedMaterial() {
        MaterialDTO updateDTO = MaterialDTO.builder()
                .id(1L)
                .name("Updated Material")
                .description("Updated description")
                .build();

        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(materialRepository.existsByNameAndIdNot("Updated Material", 1L)).thenReturn(false);
        when(materialRepository.save(any(Material.class))).thenReturn(material);
        when(modelMapper.map(material, MaterialDTO.class)).thenReturn(updateDTO);


        MaterialDTO result = materialService.updateMaterial(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Updated Material", result.getName());
        verify(materialRepository).save(any(Material.class));
    }

    @Test
    @DisplayName("Update material with duplicate name - should throw exception")
    void updateMaterial_withDuplicateName_shouldThrowException() {
        MaterialDTO updateDTO = MaterialDTO.builder()
                .id(1L)
                .name("Duplicate Name")
                .build();

        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(materialRepository.existsByNameAndIdNot("Duplicate Name", 1L)).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> materialService.updateMaterial(1L, updateDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(materialRepository, never()).save(any());
    }


    @Test
    @DisplayName("Delete existing material - should succeed")
    void deleteMaterial_withExistingId_shouldSucceed() {
        when(materialRepository.existsById(1L)).thenReturn(true);

        materialService.deleteMaterial(1L);

        verify(materialRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete non-existent material - should throw exception")
    void deleteMaterial_withNonExistentId_shouldThrowException() {
        when(materialRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> materialService.deleteMaterial(999L));

        assertEquals("Material not found with id: 999", exception.getMessage());
        verify(materialRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("Check if material exists by name - should return true")
    void existsByName_withExistingName_shouldReturnTrue() {
        when(materialRepository.existsByName("Leather")).thenReturn(true);

        boolean result = materialService.existByName("Leather");

        assertTrue(result);
        verify(materialRepository).existsByName("Leather");
    }

    @Test
    @DisplayName("Check if material exists by ID - should return true")
    void existsById_withExistingId_shouldReturnTrue() {
        when(materialRepository.existsById(1L)).thenReturn(true);

        boolean result = materialService.existsById(1L);

        assertTrue(result);
        verify(materialRepository).existsById(1L);
    }
}