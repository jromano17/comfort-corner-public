package etf.nwt.order.service;

import etf.nwt.order.dto.shipment.ShipmentDTO;
import etf.nwt.order.exception.ResourceNotFoundException;
import etf.nwt.order.models.Order;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.models.Shipment;
import etf.nwt.order.models.ShipmentStatus;
import etf.nwt.order.repository.OrderRepository;
import etf.nwt.order.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShipmentService {

    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final ModelMapper mapper;

    public ShipmentService(OrderRepository orderRepository,
                           ShipmentRepository shipmentRepository,
                           ModelMapper mapper) {
        this.orderRepository = orderRepository;
        this.shipmentRepository = shipmentRepository;
        this.mapper = mapper;
    }



    private ShipmentDTO convertToDto(Shipment shipment) {
        ShipmentDTO dto = new ShipmentDTO();

        dto.setId(shipment.getId());
        dto.setOrderId(shipment.getOrder() != null ? shipment.getOrder().getId() : null);
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setStatus(shipment.getStatus());
        dto.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        dto.setCreateDate(shipment.getCreateDate());
        return dto;
    }



    private Shipment convertToEntity(ShipmentDTO shipment, Order order) {
        Shipment entity = new Shipment();

        entity.setId(shipment.getId());
        entity.setOrder(order);
        entity.setTrackingNumber(shipment.getTrackingNumber());
        entity.setStatus(shipment.getStatus());
        entity.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        entity.setCreateDate(shipment.getCreateDate());

        return entity;
    }

    public List<ShipmentDTO> getAllShipments() {
        return shipmentRepository.findAll()
                .stream()
                //.map(shipment -> mapper.map(shipment, ShipmentDTO.class))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ShipmentDTO getShipment(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment with ID " + id + " not found"));
        return convertToDto(shipment);
        //return mapper.map(shipment, ShipmentDTO.class);
    }


    public void deleteShipment(Long id) {
        shipmentRepository.deleteById(id);
    }

    public ShipmentDTO createShipment(ShipmentDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + dto.getOrderId() + " not found"));
        dto.setCreateDate(LocalDateTime.now());
        Shipment shipment = convertToEntity(dto, order);
        shipment.setOrder(order);
        order.setShipment(shipment);
        Shipment saved = shipmentRepository.save(shipment);
        return convertToDto(saved);
    }

    public ShipmentDTO updateShipmentStatus(Long shipmentId, String newStatus) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment with ID " + shipmentId + " not found"));

        shipment.setStatus(ShipmentStatus.valueOf(newStatus));
        if ("SHIPPED".equalsIgnoreCase(newStatus)) {
            shipment.setEstimatedDeliveryDate(LocalDateTime.now());
            shipment.getOrder().setCurrentStatus(OrderStatus.SHIPPED);
        }

        return convertToDto(shipment);
        //return mapper.map(shipment,ShipmentDTO.class);
    }

    public List<ShipmentDTO> getDelayedShipments() {
        LocalDateTime currentDate = LocalDateTime.now();

        List<Shipment> shipments = shipmentRepository.findDelayedShipments(currentDate);

        return shipments.stream()
                .map(shipment -> mapper.map(shipment, ShipmentDTO.class))
                .collect(Collectors.toList());
    }

}