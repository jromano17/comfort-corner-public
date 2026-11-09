package etf.nwt.order.dto.incomeRecord;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class IncomeRecordDTO {
    private Long id;

    @NotNull
    private Long orderId;
    //@NotNull
    private LocalDateTime recordedAt;
    @NotNull
    @Positive(message = "Income amount must be positive")
    private Double incomeAmount;
    @NotNull
    private Double tax;
    @NotNull
    private Double cost;
    //@NotNull
    private Double netIncome;
}
