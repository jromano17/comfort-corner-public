package etf.nwt.order.service;


import etf.nwt.order.dto.shipment.ShipmentDTO;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.models.ShipmentStatus;
import etf.nwt.order.repository.OrderRepository;
import etf.nwt.order.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "spring.cloud.config.fail-fast=false"
})
@Transactional
class ShipmentServiceTest {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    private Order order;
    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setUserId(1L);
        order.setCurrentStatus(OrderStatus.PENDING);
        order.setTotalPrice(100.0);
        order.setCreatedAt(LocalDateTime.now());

        order = orderRepository.save(order);
    }

    @Test
    void createShipment_shouldCreateShipment() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setOrderId(order.getId());
        dto.setId(1L);
        dto.setEstimatedDeliveryDate(LocalDateTime.now());
        dto.setTrackingNumber("aaa");
        dto.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);

        ShipmentDTO created = shipmentService.createShipment(dto);

        assertNotNull(created.getId());
        assertEquals(ShipmentStatus.PREPARING, created.getStatus());
    }

    @Test
    void getShipment_shouldReturnShipment() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setOrderId(order.getId());
        dto.setOrderId(order.getId());
        dto.setId(1L);
        dto.setEstimatedDeliveryDate(LocalDateTime.now());
        dto.setTrackingNumber("aaa");
        dto.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        ShipmentDTO created = shipmentService.createShipment(dto);

        ShipmentDTO fetched = shipmentService.getShipment(created.getId());

        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    void updateShipmentStatus_shouldUpdateStatusAndOrder() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setOrderId(order.getId());
        dto.setId(1L);
        dto.setEstimatedDeliveryDate(LocalDateTime.now());
        dto.setTrackingNumber("aaa");
        dto.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        ShipmentDTO created = shipmentService.createShipment(dto);

        ShipmentDTO updated =
                shipmentService.updateShipmentStatus(created.getId(), "SHIPPED");

        assertEquals(ShipmentStatus.SHIPPED, updated.getStatus());

        Order updatedOrder =
                orderRepository.findById(order.getId()).orElseThrow();

        assertEquals(OrderStatus.SHIPPED, updatedOrder.getCurrentStatus());
    }

    @Test
    void deleteShipment_shouldRemoveShipment() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setOrderId(order.getId());
        dto.setId(1L);
        dto.setEstimatedDeliveryDate(LocalDateTime.now());
        dto.setTrackingNumber("aaa");
        dto.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        ShipmentDTO created = shipmentService.createShipment(dto);

        shipmentService.deleteShipment(created.getId());

        assertTrue(shipmentRepository.findById(created.getId()).isEmpty());
    }

    @Test
    void getAllShipments_shouldReturnList() {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setOrderId(order.getId());
        //dto.setId(1L);
        dto.setEstimatedDeliveryDate(LocalDateTime.now());
        dto.setTrackingNumber("aaa");
        dto.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        shipmentService.createShipment(dto);

        List<ShipmentDTO> shipments = shipmentService.getAllShipments();

        assertFalse(shipments.isEmpty());
    }


}
