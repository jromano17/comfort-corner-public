package etf.nwt.product.controller;

import etf.nwt.product.dto.DimensionDTO;
import etf.nwt.product.service.DimensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dimensions")
@RequiredArgsConstructor
public class DimensionController {

    private final DimensionService dimensionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DimensionDTO> createDimension(@Valid @RequestBody DimensionDTO dimensionDTO) {
        DimensionDTO created = dimensionService.createDimension(dimensionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DimensionDTO> getDimensionById(@PathVariable Long id) {
        DimensionDTO dimension = dimensionService.getDimensionById(id);
        return ResponseEntity.ok(dimension);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DimensionDTO> getDimensionByName(@PathVariable String name) {
        DimensionDTO dimension = dimensionService.getDimensionByName(name);
        return ResponseEntity.ok(dimension);
    }

    @GetMapping
    public ResponseEntity<List<DimensionDTO>> getAllDimensions() {
        List<DimensionDTO> dimensions = dimensionService.getAllDimensions();
        return ResponseEntity.ok(dimensions);
    }


    @GetMapping("/search")
    public ResponseEntity<List<DimensionDTO>> searchDimensions(@RequestParam String keyword) {
        List<DimensionDTO> dimensions = dimensionService.searchDimensions(keyword);
        return ResponseEntity.ok(dimensions);
    }

    @GetMapping("/size-range")
    public ResponseEntity<List<DimensionDTO>> getDimensionsBySizeRange(
            @RequestParam Double minWidth,
            @RequestParam Double maxWidth,
            @RequestParam Double minHeight,
            @RequestParam Double maxHeight,
            @RequestParam Double minDepth,
            @RequestParam Double maxDepth) {

        List<DimensionDTO> dimensions = dimensionService.getDimensionsBySizeRange(
                minWidth, maxWidth,
                minHeight, maxHeight,
                minDepth, maxDepth);

        return ResponseEntity.ok(dimensions);
    }

    @GetMapping("/weight-capacity")
    public ResponseEntity<List<DimensionDTO>> getDimensionsByMinWeightCapacity(
            @RequestParam Double minWeightCapacity) {
        List<DimensionDTO> dimensions = dimensionService.getDimensionsByMinWeightCapacity(minWeightCapacity);
        return ResponseEntity.ok(dimensions);
    }


    @GetMapping("/stats/sizes")
    public ResponseEntity<Object> getSizeStatistics() {
        Object stats = dimensionService.getSizeStatistics();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DimensionDTO> updateDimension(
            @PathVariable Long id,
            @Valid @RequestBody DimensionDTO dimensionDTO) {
        DimensionDTO updated = dimensionService.updateDimension(id, dimensionDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteDimension(@PathVariable Long id) {
        dimensionService.deleteDimension(id);
        return ResponseEntity.noContent().build();
    }
}
