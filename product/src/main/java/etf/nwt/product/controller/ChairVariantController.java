package etf.nwt.product.controller;


import etf.nwt.product.dto.ChairVariantDTO;
import etf.nwt.product.dto.CreateChairVariantDTO;
import etf.nwt.product.dto.StockCheckResponseDTO;
import etf.nwt.product.exception.ResourceNotFoundException;
import etf.nwt.product.model.Chair;
import etf.nwt.product.service.ChairVariantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chair-variants")
@RequiredArgsConstructor
public class ChairVariantController {

    private final ChairVariantService chairVariantService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChairVariantDTO> createChairVariant(
            @Valid @RequestBody CreateChairVariantDTO chairVariantDTO) {
        ChairVariantDTO created = chairVariantService.createChairVariant(chairVariantDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChairVariantDTO> getChairVariantById(@PathVariable Long id) {
        ChairVariantDTO chairVariant = chairVariantService.getChairVariantById(id);
        return ResponseEntity.ok(chairVariant);
    }

    @GetMapping
    public ResponseEntity<List<ChairVariantDTO>> getAllChairVariants() {
        List<ChairVariantDTO> variants = chairVariantService.getAllChairVariants();
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/chair/{chairId}")
    public ResponseEntity<List<ChairVariantDTO>> getVariantsByChairId(@PathVariable Long chairId) {
        List<ChairVariantDTO> variants = chairVariantService.getVariantsByChairId(chairId);
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<List<ChairVariantDTO>> getInStockVariants() {
        List<ChairVariantDTO> variants = chairVariantService.getInStockVariants();
        return ResponseEntity.ok(variants);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChairVariantDTO> updateChairVariant(
            @PathVariable Long id,
            @Valid @RequestBody ChairVariantDTO chairVariantDTO) {
        chairVariantDTO.setId(id);
        ChairVariantDTO updated = chairVariantService.updateChairVariant(chairVariantDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChairVariantDTO> updateStockQuantity(
            @PathVariable Long id,
            @RequestParam @Min(value = 0, message = "Stock quantity cannot be negative") Integer quantity) {
        ChairVariantDTO updated = chairVariantService.updateStockQuantity(id, quantity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteChairVariant(@PathVariable Long id) {
        chairVariantService.deleteChairVariant(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chair/{chairId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteVariantsByChairId(@PathVariable Long chairId) {
        chairVariantService.deleteVariantsByChairId(chairId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{variantId}/check-stock")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<StockCheckResponseDTO> checkStock(@PathVariable Long variantId) {
        StockCheckResponseDTO response = chairVariantService.getVariantStockData(variantId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-stock-batch")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<StockCheckResponseDTO>> checkStockBatch(@RequestBody List<Long> variantIds) {
        List<StockCheckResponseDTO> response = chairVariantService.getVariantStockData(variantIds);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{id}/image")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> uploadVariantImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(chairVariantService.uploadImage(id,file));
    }
}