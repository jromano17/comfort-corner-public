package etf.nwt.product.controller;

import etf.nwt.product.dto.CategoryDTO;
import etf.nwt.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/search")
    public ResponseEntity<List<CategoryDTO>> searchCategories(@RequestParam String keyword) {
        List<CategoryDTO> results = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(results);
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO created = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(
            @RequestParam(required = false) Boolean includeChairs) {
        List<CategoryDTO> categories;
        if (Boolean.TRUE.equals(includeChairs)) {
            categories = categoryService.getAllCategoriesWithChairs();
        } else {
            categories = categoryService.getAllCategories();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String name) {
        CategoryDTO category = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/{id}/chairs/count")
    public ResponseEntity<Long> getChairCount(@PathVariable Long id) {
        Long count = categoryService.getChairCount(id);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setId(id);
        CategoryDTO updated = categoryService.updateCategory(categoryDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/name")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategoryName(
            @PathVariable Long id,
            @RequestParam String name) {
        CategoryDTO updated = categoryService.updateCategoryName(id, name);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/description")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategoryDescription(
            @PathVariable Long id,
            @RequestParam String description) {
        CategoryDTO updated = categoryService.updateCategoryDescription(id, description);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/force")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> forceDeleteCategory(@PathVariable Long id) {
        categoryService.forceDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{categoryId}/chairs/{chairId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO> addChairToCategory(
            @PathVariable Long categoryId,
            @PathVariable Long chairId) {
        CategoryDTO updated = categoryService.addChairToCategory(categoryId, chairId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{categoryId}/chairs/{chairId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO> removeChairFromCategory(
            @PathVariable Long categoryId,
            @PathVariable Long chairId) {
        CategoryDTO updated = categoryService.removeChairFromCategory(categoryId, chairId);
        return ResponseEntity.ok(updated);
    }
}