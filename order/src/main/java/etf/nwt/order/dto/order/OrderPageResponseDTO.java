package etf.nwt.order.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageResponseDTO {
    private List<OrderSummaryDTO> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private SortInfo sort;

    @Data
    @Builder
    public static class SortInfo {
        private String property;
        private String direction;
        private boolean sorted;
    }
}