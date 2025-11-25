package apap.ti._5.tour_package_2306275203_be.model.loyalty;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "loyalty_points")
public class LoyaltyPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;
}