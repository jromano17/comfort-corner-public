package etf.nwt.order.controller;

import etf.nwt.order.dto.shipment.ShipmentDTO;
import etf.nwt.order.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<ShipmentDTO> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ShipmentDTO getShipment(@PathVariable Long id) {
        return shipmentService.getShipment(id);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ShipmentDTO createShipment(@Valid @RequestBody ShipmentDTO dto) {
        return shipmentService.createShipment(dto);
    }

    @PatchMapping("/{shipmentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ShipmentDTO updateShipmentStatus(@PathVariable Long shipmentId,
                                            @Valid @RequestParam String status) {
        return shipmentService.updateShipmentStatus(shipmentId, status);
    }

    @GetMapping("/delayed")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<ShipmentDTO> getDelayedShipments() {
        return shipmentService.getDelayedShipments();
    }
}