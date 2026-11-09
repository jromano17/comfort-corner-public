package etf.nwt.order.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "income_records")
public class IncomeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private LocalDateTime recordedAt;

    private Double incomeAmount;
    private Double tax;
    private Double cost;
    private Double netIncome;      // incomeAmount - tax - cost
}