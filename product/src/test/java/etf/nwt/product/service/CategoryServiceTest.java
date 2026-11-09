package etf.nwt.product.service;

import etf.nwt.product.dto.CategoryDTO;
import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.Category;
import etf.nwt.product.model.Chair;
import etf.nwt.product.repository.CategoryRepository;
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
@DisplayName("Category Service Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ChairRepository chairRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;
    private Chair chair1;
    private Chair chair2;

    @BeforeEach
    void setUp() {
        // Setup test data
        chair1 = Chair.builder()
                .id(1L)
                .name("Office Chair")
                .description("professional")
                .basePrice(new BigDecimal("199.99"))
                .build();

        chair2 = Chair.builder()
                .id(2L)
                .name("Gaming Chair")
                .description("Comfy")
                .basePrice(new BigDecimal("299.99"))
                .build();

        category = Category.builder()
                .id(1L)
                .name("Office Furniture")
                .description("Chairs and desks for office")
                .chairs(new ArrayList<>(Arrays.asList(chair1, chair2)))
                .build();

        categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Office Furniture")
                .description("Chairs and desks for office")
                .build();
    }


    @Test
    @DisplayName("Create category with valid data - should succeed")
    void createCategory_withValidData_shouldReturnCategoryDTO() {
        CategoryDTO requestDTO = CategoryDTO.builder()
                .name("New Category")
                .description("New category description")
                .build();

        when(categoryRepository.existsByNameIgnoreCase("New Category")).thenReturn(false);
        when(modelMapper.map(requestDTO, Category.class)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(requestDTO);

        assertNotNull(result);
        assertEquals("Office Furniture", result.getName());
        verify(categoryRepository).save(any(Category.class));
        verify(categoryRepository).existsByNameIgnoreCase("New Category");
    }

    @Test
    @DisplayName("Create category with duplicate name - should throw exception")
    void createCategory_withDuplicateName_shouldThrowException() {
        CategoryDTO requestDTO = CategoryDTO.builder()
                .name("Duplicate Category")
                .build();

        when(categoryRepository.existsByNameIgnoreCase("Duplicate Category")).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> categoryService.createCategory(requestDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(categoryRepository, never()).save(any());
    }


    @Test
    @DisplayName("Get category by existing ID - should return category")
    void getCategoryById_withExistingId_shouldReturnCategoryDTO() {
        when(categoryRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Office Furniture", result.getName());
        verify(categoryRepository).findByIdWithChairs(1L);
    }

    @Test
    @DisplayName("Get category by non-existent ID - should throw exception")
    void getCategoryById_withNonExistentId_shouldThrowException() {
        when(categoryRepository.findByIdWithChairs(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(999L));

        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryRepository).findByIdWithChairs(999L);
    }

    @Test
    @DisplayName("Get all categories - should return list")
    void getAllCategories_shouldReturnList() {
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Office Furniture", result.get(0).getName());
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Get category by name - should return category")
    void getCategoryByName_withExistingName_shouldReturnCategoryDTO() {
        when(categoryRepository.findByNameIgnoreCase("Office Furniture")).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryByName("Office Furniture");

        assertNotNull(result);
        assertEquals("Office Furniture", result.getName());
        verify(categoryRepository).findByNameIgnoreCase("Office Furniture");
    }


    @Test
    @DisplayName("Update category with valid data - should succeed")
    void updateCategory_withValidData_shouldReturnUpdatedCategory() {
        CategoryDTO updateDTO = CategoryDTO.builder()
                .id(1L)
                .name("Updated Category")
                .description("Updated description")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Updated Category", 1L)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(updateDTO);

        CategoryDTO result = categoryService.updateCategory(updateDTO);

        assertNotNull(result);
        assertEquals("Updated Category", result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Update category with duplicate name - should throw exception")
    void updateCategory_withDuplicateName_shouldThrowException() {
        CategoryDTO updateDTO = CategoryDTO.builder()
                .id(1L)
                .name("Duplicate Name")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Duplicate Name", 1L)).thenReturn(true);

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> categoryService.updateCategory(updateDTO));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(categoryRepository, never()).save(any());
    }


    @Test
    @DisplayName("Delete empty category - should succeed")
    void deleteCategory_withNoChairs_shouldSucceed() {
        Category emptyCategory = Category.builder()
                .id(1L)
                .name("Empty Category")
                .build();

        when(categoryRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(emptyCategory));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(emptyCategory);
    }

    @Test
    @DisplayName("Delete category with chairs - should throw exception")
    void deleteCategory_withChairs_shouldThrowException() {
        when(categoryRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(category));

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> categoryService.deleteCategory(1L));

        assertTrue(exception.getMessage().contains("Cannot delete category with chairs"));
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Force delete category - should succeed")
    void forceDeleteCategory_shouldSucceed() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.forceDeleteCategory(1L);

        verify(categoryRepository).delete(category);
    }


    @Test
    @DisplayName("Add chair to category - should succeed")
    void addChairToCategory_shouldSucceed() {
        when(categoryRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(category));
        when(chairRepository.findById(3L)).thenReturn(Optional.of(
                Chair.builder().id(3L).name("New Chair").build()
        ));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.addChairToCategory(1L, 3L);

        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Remove chair from category - should succeed")
    void removeChairFromCategory_shouldSucceed() {
        when(categoryRepository.findByIdWithChairs(1L)).thenReturn(Optional.of(category));
        when(chairRepository.findById(1L)).thenReturn(Optional.of(chair1));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.removeChairFromCategory(1L, 1L);

        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Get chair count - should return correct count")
    void getChairCount_shouldReturnCount() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.countChairsByCategoryId(1L)).thenReturn(2L);

        Long count = categoryService.getChairCount(1L);

        assertEquals(2L, count);
        verify(categoryRepository).countChairsByCategoryId(1L);
    }

    @Test
    @DisplayName("Search categories - should return matching categories")
    void searchCategories_shouldReturnMatching() {
        String keyword = "office";
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.searchByNameOrDescription(keyword)).thenReturn(categories);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.searchCategories(keyword);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository).searchByNameOrDescription(keyword);
    }
}