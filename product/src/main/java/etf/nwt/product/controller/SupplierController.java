package etf.nwt.product.controller;

import etf.nwt.product.dto.SupplierDTO;
import etf.nwt.product.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO created = supplierService.createSupplier(supplierDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers(
            @RequestParam(required = false) Boolean includeChairs,
            @RequestParam(required = false) String country) {
        List<SupplierDTO> suppliers;

        if (country != null) {
            suppliers = supplierService.getSuppliersByCountry(country);
        } else if (Boolean.TRUE.equals(includeChairs)) {
            suppliers = supplierService.getAllSuppliersWithChairs();
        } else {
            suppliers = supplierService.getAllSuppliers();
        }

        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByCountry(@PathVariable String country) {
        List<SupplierDTO> suppliers = supplierService.getSuppliersByCountry(country);
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierDTO> getSupplierByName(@PathVariable String name) {
        SupplierDTO supplier = supplierService.getSupplierByName(name);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<SupplierDTO> getSupplierByEmail(@PathVariable String email) {
        SupplierDTO supplier = supplierService.getSupplierByEmail(email);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping("/{id}/chairs/count")
    public ResponseEntity<Long> getChairCount(@PathVariable Long id) {
        Long count = supplierService.getChairCount(id);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/country")
    public ResponseEntity<Object> getSupplierStatsByCountry() {
        Object stats = supplierService.getSupplierStatsByCountry();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierDTO supplierDTO) {
        supplierDTO.setId(id);
        SupplierDTO updated = supplierService.updateSupplier(supplierDTO);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/contact-email")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SupplierDTO> updateSupplierEmail(
            @PathVariable Long id,
            @RequestParam @jakarta.validation.constraints.Email String email) {
        SupplierDTO updated = supplierService.updateSupplierEmail(id, email);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/country")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SupplierDTO> updateSupplierCountry(
            @PathVariable Long id,
            @RequestParam String country) {
        SupplierDTO updated = supplierService.updateSupplierCountry(id, country);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/force")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> forceDeleteSupplier(@PathVariable Long id) {
        supplierService.forceDeleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{supplierId}/chairs/{chairId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SupplierDTO> addChairToSupplier(
            @PathVariable Long supplierId,
            @PathVariable Long chairId) {
        SupplierDTO updated = supplierService.addChairToSupplier(supplierId, chairId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{supplierId}/chairs/{chairId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SupplierDTO> removeChairFromSupplier(
            @PathVariable Long supplierId,
            @PathVariable Long chairId) {
        SupplierDTO updated = supplierService.removeChairFromSupplier(supplierId, chairId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "false") Boolean includeChairs) {
        List<SupplierDTO> suppliers = supplierService.searchSuppliers(keyword, includeChairs);
        return ResponseEntity.ok(suppliers);
    }
}