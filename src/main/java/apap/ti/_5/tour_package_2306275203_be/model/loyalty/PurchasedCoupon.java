package apap.ti._5.tour_package_2306275203_be.model.loyalty;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "purchased_coupon")
public class PurchasedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @CreationTimestamp
    @Column(name = "purchased_date", nullable = false, updatable = false)
    private LocalDateTime purchasedDate;

    @Column(name = "used_date")
    private LocalDateTime usedDate;
}