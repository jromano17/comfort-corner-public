package etf.nwt.product.controller;

import etf.nwt.product.dto.MaterialDTO;
import etf.nwt.product.exception.InvalidOperationException;
import etf.nwt.product.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MaterialDTO> createMaterial(@Valid @RequestBody MaterialDTO materialDTO) {
        MaterialDTO created = materialService.createMaterial(materialDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> getMaterialById(@PathVariable Long id) {
        MaterialDTO material = materialService.getMaterialById(id);
        return ResponseEntity.ok(material);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<MaterialDTO> getMaterialByName(@PathVariable String name) {
        MaterialDTO material = materialService.getMaterialByName(name);
        return ResponseEntity.ok(material);
    }

    @GetMapping
    public ResponseEntity<List<MaterialDTO>> getAllMaterials() {
        List<MaterialDTO> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MaterialDTO>> searchMaterials(@RequestParam String keyword) {
        List<MaterialDTO> materials = materialService.searchMaterials(keyword);
        return ResponseEntity.ok(materials);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MaterialDTO> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody MaterialDTO materialDTO) {

        if (materialDTO.getId() != null && !id.equals(materialDTO.getId())) {
            throw new InvalidOperationException("ID in path must match ID in request body");
        }
        MaterialDTO updated = materialService.updateMaterial(id, materialDTO);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}