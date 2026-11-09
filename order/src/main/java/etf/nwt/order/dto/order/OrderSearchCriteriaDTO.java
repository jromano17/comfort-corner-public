package etf.nwt.order.dto.order;

import etf.nwt.order.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCriteriaDTO {
    private Long userId;
    private OrderStatus status;
    private Double minAmount;
    private Double maxAmount;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean hasShipment;
    private String search;

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasStatus() {
        return status != null;
    }

    public boolean hasAmountRange() {
        return minAmount != null || maxAmount != null;
    }

    public boolean hasDateRange() {
        return fromDate != null || toDate != null;
    }

    public boolean hasShipmentFilter() {
        return hasShipment != null;
    }

    public boolean hasSearch() {
        return search != null && !search.trim().isEmpty();
    }
}