package etf.nwt.order.dto.rabbit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutOfStockEvent {
    private Long orderId;
    private String reason;
}