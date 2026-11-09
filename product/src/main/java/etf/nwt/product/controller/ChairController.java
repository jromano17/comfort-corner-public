package etf.nwt.product.controller;

import etf.nwt.product.dto.ChairDTO;
import etf.nwt.product.model.Chair;
import etf.nwt.product.service.ChairService;
import etf.nwt.product.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chairs")
public class ChairController {

    private final ChairService chairService;

    @Autowired
    public ChairController(ChairService chairService) {
        this.chairService = chairService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChairDTO> createChair(@Valid @RequestBody ChairDTO chairDTO) {
        ChairDTO created = chairService.createChair(chairDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChairDTO> getChair(@PathVariable Long id) {
        ChairDTO chair = chairService.getChairById(id);
        return ResponseEntity.ok(chair);
    }

    @GetMapping
    public ResponseEntity<List<ChairDTO>> getAllChairs() {
        List<ChairDTO> chairs = chairService.getAllChairs();
        return ResponseEntity.ok(chairs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChairDTO> updateChair(@PathVariable Long id,
                                                @Valid @RequestBody ChairDTO chairDTO) {
        chairDTO.setId(id);
        ChairDTO updated = chairService.updateChair(chairDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteChair(@PathVariable Long id) {
        chairService.deleteChair(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/gallery")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> uploadGallery(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(chairService.uploadImage(id,file));
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<String>> uploadMultipleImages(@PathVariable Long id, @RequestParam("images") List<MultipartFile> files) {
        return ResponseEntity.ok(chairService.uploadMultipleImages(id, files));
    }
}