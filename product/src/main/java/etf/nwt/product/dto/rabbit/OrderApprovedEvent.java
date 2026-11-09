package etf.nwt.product.dto.rabbit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderApprovedEvent {
    private Long orderId;
    private Map<Long, Integer> items;
}