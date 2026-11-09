package etf.nwt.order.controller;

import etf.nwt.order.dto.order.OrderDTO;
import etf.nwt.order.dto.orderItem.OrderItemDTO;
import etf.nwt.order.dto.orderStatusHistory.OrderStatusHistoryDTO;
import etf.nwt.order.models.OrderStatus;
import etf.nwt.order.service.OrderItemService;
import etf.nwt.order.service.OrderService;
import etf.nwt.order.service.OrderStatusHistoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderStatusHistoryService historyService;

    public OrderController(OrderService orderService,
                           OrderItemService orderItemService,
                           OrderStatusHistoryService historyService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.historyService = historyService;
    }


    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public OrderDTO createOrder(@Valid @RequestBody OrderDTO dto,@RequestHeader("X-Logged-In-User") Long usernameId) {
        dto.setUserId(usernameId);
        return orderService.createOrder(dto);
    }



    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public OrderDTO updateStatus(@PathVariable Long orderId,
                                 @RequestParam OrderStatus newStatus,
                                 @RequestParam(required = false) Long changedBy) {

        return historyService.recordStatusChange(orderId, newStatus, changedBy);
    }



    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<OrderDTO> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        System.out.println("Order controller");
        return orderService.getAllOrders(page, size, sort);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<OrderDTO> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return orderService.getOrdersByStatus(status, page, size);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<OrderDTO> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return orderService.getOrdersByUser(userId, page, size);
    }

    @GetMapping("/high-value-pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<OrderDTO> getHighValuePendingOrders(
            @RequestParam(defaultValue = "500.0") @Min(0) Double minAmount,
            @RequestParam(defaultValue = "7") @Min(1) @Max(90) Integer daysBack) {

        return orderService.getHighValuePendingOrders(minAmount, daysBack);
    }
    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Page<OrderDTO> getMyOrders(@RequestHeader("X-Logged-In-User") Long usernameId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersByUser(usernameId,page,size);
    }
/*
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public OrderDTO getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public OrderDTO updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDTO dto) {
        return orderService.updateOrder(id, dto);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public List<OrderItemDTO> getItems(@PathVariable Long orderId) {
        return orderItemService.getItemsByOrder(orderId);
    }

    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public OrderItemDTO addItem(@PathVariable Long orderId,
                                @Valid @RequestBody OrderItemDTO itemDTO) {
        return orderItemService.addItemToOrder(orderId, itemDTO);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public void removeItem(@PathVariable Long orderId,
                           @PathVariable Long itemId) {
        orderItemService.removeItem(orderId, itemId);
    }
    @GetMapping("/{orderId}/status-history")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public List<OrderStatusHistoryDTO> getStatusHistory(@PathVariable Long orderId) {
        return historyService.getHistoryByOrderId(orderId);
    }
*/
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public OrderDTO getOrder(
            @PathVariable Long id,
            @RequestHeader("X-Logged-In-User") Long usernameId,
            @RequestHeader("X-Logged-In-Role") String role) {

        return orderService.getOrderSecurely(id, usernameId, role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public void deleteOrder(
            @PathVariable Long id,
            @RequestHeader("X-Logged-In-User") Long usernameId,
            @RequestHeader("X-Logged-In-Role") String role) {
        orderService.deleteOrderSecurely(id, usernameId, role);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public OrderDTO updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderDTO dto,
            @RequestHeader("X-Logged-In-User") Long usernameId,
            @RequestHeader("X-Logged-In-Role") String role) {
        return orderService.updateOrderSecurely(id, dto, usernameId, role);
    }
    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public List<OrderItemDTO> getItems(
            @PathVariable Long orderId,
            @RequestHeader("X-Logged-In-User") Long usernameId,
            @RequestHeader("X-Logged-In-Role") String role) {
        return orderItemService.getItemsByOrderSecurely(orderId, usernameId, role);
    }

    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public OrderItemDTO addItem(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemDTO dto,
            @RequestHeader("X-Logged-In-User") Long usernameId,
            @RequestHeader("X-Logged-In-Role") String role) {

        return orderItemService.addItemToOrderSecurely(orderId, dto, usernameId, role);

    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public void removeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestHeader("X-Logged-In-User") Long usernameId,
            @RequestHeader("X-Logged-In-Role") String role) {
        orderItemService.removeItemSecurely(orderId, itemId, usernameId, role);
    }

    @GetMapping("/{orderId}/status-history")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public List<OrderStatusHistoryDTO> getStatusHistory(
            @PathVariable Long orderId,
            @RequestHeader("X-Logged-In-User") Long usernameId,
            @RequestHeader("X-Logged-In-Role") String role) {
        return historyService.getHistoryByOrderIdSecurely(orderId, usernameId, role);
    }


    @GetMapping("/{orderId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public OrderStatus getOrderStatus(
            @PathVariable Long orderId,
            @RequestHeader("X-Logged-In-User") Long usernameId,
            @RequestHeader("X-Logged-In-Role") String role) {
        return historyService.getOrderStatus(orderId, usernameId, role);
    }
}
