package etf.nwt.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import etf.nwt.order.dto.shipment.ShipmentDTO;
import etf.nwt.order.models.ShipmentStatus;
import etf.nwt.order.service.ShipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ActiveProfiles("test")
@WebMvcTest(ShipmentController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.fail-fast=false",
        "spring.config.import=",
        "eureka.client.enabled=false"
})
class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShipmentService shipmentService;

    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;
    private ShipmentDTO shipmentDTO;

    @BeforeEach
    void setUp() {
        shipmentDTO = new ShipmentDTO();
        shipmentDTO.setId(1L);
        shipmentDTO.setOrderId(10L);
        shipmentDTO.setStatus(ShipmentStatus.PREPARING);
        shipmentDTO.setTrackingNumber("aaaaa");
        shipmentDTO.setEstimatedDeliveryDate(LocalDateTime.now());
        shipmentDTO.setCreateDate(LocalDateTime.now());


        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void getAllShipments_shouldReturnList() throws Exception {
        when(shipmentService.getAllShipments()).thenReturn(List.of(shipmentDTO));

        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getShipment_shouldReturnShipment() throws Exception {
        when(shipmentService.getShipment(1L)).thenReturn(shipmentDTO);

        mockMvc.perform(get("/api/shipments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createShipment_shouldReturnCreatedShipment() throws Exception {
        when(shipmentService.createShipment(any(ShipmentDTO.class)))
                .thenReturn(shipmentDTO);

        mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shipmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateShipmentStatus_shouldUpdateStatus() throws Exception {
        shipmentDTO.setStatus(ShipmentStatus.SHIPPED);

        when(shipmentService.updateShipmentStatus(1L, "SHIPPED"))
                .thenReturn(shipmentDTO);

        mockMvc.perform(patch("/api/shipments/1")
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void deleteShipment_shouldReturnOk() throws Exception {
        doNothing().when(shipmentService).deleteShipment(1L);

        mockMvc.perform(delete("/api/shipments/1"))
                .andExpect(status().isOk());
    }
    @Test
    void getDelayedShipments_shouldReturnDelayedShipments() throws Exception {
        // Arrange
        ShipmentDTO delayedShipment1 = new ShipmentDTO();
        delayedShipment1.setId(1L);
        delayedShipment1.setTrackingNumber("TRACK123");
        delayedShipment1.setStatus(ShipmentStatus.SHIPPED);
        delayedShipment1.setEstimatedDeliveryDate(LocalDateTime.now().minusDays(2));
        delayedShipment1.setOrderId(100L);

        ShipmentDTO delayedShipment2 = new ShipmentDTO();
        delayedShipment2.setId(2L);
        delayedShipment2.setTrackingNumber("TRACK456");
        delayedShipment2.setStatus(ShipmentStatus.PROCESSING);
        delayedShipment2.setEstimatedDeliveryDate(LocalDateTime.now().minusDays(1));
        delayedShipment2.setOrderId(200L);

        List<ShipmentDTO> delayedShipments = Arrays.asList(delayedShipment1, delayedShipment2);

        when(shipmentService.getDelayedShipments())
                .thenReturn(delayedShipments);

        // Act & Assert
        mockMvc.perform(get("/api/shipments/delayed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trackingNumber").value("TRACK123"))
                .andExpect(jsonPath("$[0].status").value("SHIPPED"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].trackingNumber").value("TRACK456"))
                .andExpect(jsonPath("$[1].status").value("PROCESSING"));

        verify(shipmentService).getDelayedShipments();
    }

    @Test
    void getDelayedShipments_whenNoDelayedShipments_shouldReturnEmptyList() throws Exception {
        when(shipmentService.getDelayedShipments())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/shipments/delayed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(shipmentService).getDelayedShipments();
    }

}
