package etf.nwt.system_events.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_events")
@Getter
@Setter
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String microserviceName;
    private String userUsername;
    private String actionType;
    private String resourceName;
    private String responseType;
}