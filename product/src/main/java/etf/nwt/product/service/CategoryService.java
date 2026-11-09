package etf.nwt.product.service;

import etf.nwt.product.dto.CategoryDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.model.Chair;
import etf.nwt.product.model.Category;
import etf.nwt.product.repository.CategoryRepository;
import etf.nwt.product.repository.ChairRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ChairRepository chairRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Creating category: {}", categoryDTO.getName());

        if (categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
            throw new InvalidOperationException(
                    "Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = modelMapper.map(categoryDTO, Category.class);

        Category saved = categoryRepository.save(category);
        log.info("Category created with ID: {}", saved.getId());

        return  modelMapper.map(category, CategoryDTO.class);
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        log.debug("Fetching category by ID: {}", id);

        Category category = categoryRepository.findByIdWithChairs(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));

        return modelMapper.map(category, CategoryDTO.class);
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryByName(String name) {
        log.debug("Fetching category by name: {}", name);

        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with name: " + name));

        return modelMapper.map(category, CategoryDTO.class);
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.debug("Fetching all categories");

        return categoryRepository.findAll()
                .stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategoriesWithChairs() {
        log.debug("Fetching all categories with chairs");

        return categoryRepository.findAllWithChairs()
                .stream()
                .map(category ->  modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> searchCategories(String keyword) {
        log.debug("Searching categories with keyword: {}", keyword);

        return categoryRepository.searchByNameOrDescription(keyword)
                .stream()
                .map(category ->  modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getChairCount(Long categoryId) {
        log.debug("Getting chair count for category ID: {}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }

        return categoryRepository.countChairsByCategoryId(categoryId);
    }

    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        log.info("Updating category ID: {}", categoryDTO.getId());

        Category existing = categoryRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryDTO.getId()));

        if (!existing.getName().equalsIgnoreCase(categoryDTO.getName()) &&
                categoryRepository.existsByNameIgnoreCaseAndIdNot(
                        categoryDTO.getName(), existing.getId())) {
            throw new InvalidOperationException(
                    "Category with name '" + categoryDTO.getName() + "' already exists");
        }

        existing.setName(categoryDTO.getName());
        existing.setDescription(categoryDTO.getDescription());
/*
        if (categoryDTO.getChairIds() != null && !categoryDTO.getChairIds().isEmpty()) {
            updateCategoryChairs(existing, categoryDTO.getChairIds());
        }
*/
        Category updated = categoryRepository.save(existing);
        log.info("Category updated successfully: {}", updated.getId());

        return modelMapper.map(updated, CategoryDTO.class);
    }

    @Transactional
    public CategoryDTO updateCategoryName(Long id, String name) {
        log.info("Updating category name for ID: {} to: {}", id, name);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));

        if (!category.getName().equalsIgnoreCase(name) &&
                categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new InvalidOperationException(
                    "Category with name '" + name + "' already exists");
        }

        category.setName(name);
        Category updated = categoryRepository.save(category);

        return modelMapper.map(updated, CategoryDTO.class);
    }

    @Transactional
    public CategoryDTO updateCategoryDescription(Long id, String description) {
        log.info("Updating category description for ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));

        category.setDescription(description);
        Category updated = categoryRepository.save(category);

        return modelMapper.map(updated, CategoryDTO.class);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category ID: {}", id);

        Category category = categoryRepository.findByIdWithChairs(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));

        if (category.getChairs() != null && !category.getChairs().isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot delete category with chairs. " +
                            "Please remove all chairs first or use force delete.");
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", id);
    }

    @Transactional
    public void forceDeleteCategory(Long id) {
        log.warn("Force deleting category ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));

        try {
            categoryRepository.delete(category);
            log.warn("Category force deleted: {}", id);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidOperationException(
                    "Cannot delete category due to foreign key constraints. " +
                            "Please remove or reassign related chairs first.");
        }
    }

    @Transactional
    public CategoryDTO addChairToCategory(Long categoryId, Long chairId) {
        log.info("Adding chair {} to category {}", chairId, categoryId);

        Category category = categoryRepository.findByIdWithChairs(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair not found with id: " + chairId));

        if (category.getChairs().contains(chair)) {
            throw new InvalidOperationException(
                    "Chair is already in this category");
        }

        category.getChairs().add(chair);
        chair.setCategory(category);

        Category updated = categoryRepository.save(category);

        return modelMapper.map(updated, CategoryDTO.class);
    }

    @Transactional
    public CategoryDTO removeChairFromCategory(Long categoryId, Long chairId) {
        log.info("Removing chair {} from category {}", chairId, categoryId);

        Category category = categoryRepository.findByIdWithChairs(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));

        Chair chair = chairRepository.findById(chairId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chair not found with id: " + chairId));

        if (!category.getChairs().contains(chair)) {
            throw new InvalidOperationException(
                    "Chair is not in this category");
        }

        category.getChairs().remove(chair);
        chair.setCategory(null);

        Category updated = categoryRepository.save(category);

        return modelMapper.map(updated, CategoryDTO.class);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    private void updateCategoryChairs(Category category, List<Long> chairIds) {
        for (Chair chair : category.getChairs()) {
            chair.setCategory(null);
        }
        category.getChairs().clear();

        for (Long chairId : chairIds) {
            Chair chair = chairRepository.findById(chairId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Chair not found with id: " + chairId));
            category.getChairs().add(chair);
            chair.setCategory(category);
        }
    }

   /* private CategoryDTO toDto(Category category, boolean includeChairs) {
        CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
//
        if (includeChairs && category.getChairs() != null) {
            List<ChairDTO> chairDTOs = category.getChairs().stream()
                    .map(chair -> modelMapper.map(chair, ChairDTO.class))
                    .collect(Collectors.toList());
            dto.setChairs(chairDTOs);
        }//

    return dto;
    }*/
}