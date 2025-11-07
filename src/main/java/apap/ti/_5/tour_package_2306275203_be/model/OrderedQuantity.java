package apap.ti._5.tour_package_2306275203_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordered_quantity")
@SQLDelete(sql = "UPDATE ordered_quantity SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class OrderedQuantity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ordered_quota", nullable = false)
    private int orderedQuota; 

    @Column(name = "quota", nullable = false)
    private int quota;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", referencedColumnName = "id")
    private Activity activity;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
