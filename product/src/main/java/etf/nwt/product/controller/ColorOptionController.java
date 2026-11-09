package etf.nwt.product.controller;

import etf.nwt.product.dto.ColorOptionDTO;
import etf.nwt.product.service.ColorOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/color-options")
@RequiredArgsConstructor
public class ColorOptionController {

    private final ColorOptionService colorOptionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ColorOptionDTO> createColorOption(@Valid @RequestBody ColorOptionDTO colorOptionDTO) {
        ColorOptionDTO created = colorOptionService.createColorOption(colorOptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColorOptionDTO> getColorOptionById(@PathVariable Long id) {
        ColorOptionDTO colorOption = colorOptionService.getColorOptionById(id);
        return ResponseEntity.ok(colorOption);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ColorOptionDTO> getColorOptionByName(@PathVariable String name) {
        ColorOptionDTO colorOption = colorOptionService.getColorOptionByName(name);
        return ResponseEntity.ok(colorOption);
    }

    @GetMapping("/hex/{hexCode}")
    public ResponseEntity<ColorOptionDTO> getColorOptionByHexCode(@PathVariable String hexCode) {
        ColorOptionDTO colorOption = colorOptionService.getColorOptionByHexCode(hexCode);
        return ResponseEntity.ok(colorOption);
    }

    @GetMapping
    public ResponseEntity<List<ColorOptionDTO>> getAllColorOptions() {
        List<ColorOptionDTO> colorOptions;
        colorOptions = colorOptionService.getAllColorOptions();
        return ResponseEntity.ok(colorOptions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ColorOptionDTO>> searchColorOptions(@RequestParam String keyword) {
        List<ColorOptionDTO> colorOptions = colorOptionService.searchColorOptions(keyword);
        return ResponseEntity.ok(colorOptions);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ColorOptionDTO> updateColorOption(
            @PathVariable Long id,
            @Valid @RequestBody ColorOptionDTO colorOptionDTO) {
        ColorOptionDTO updated = colorOptionService.updateColorOption(id, colorOptionDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteColorOption(@PathVariable Long id) {
        colorOptionService.deleteColorOption(id);
        return ResponseEntity.noContent().build();
    }
}