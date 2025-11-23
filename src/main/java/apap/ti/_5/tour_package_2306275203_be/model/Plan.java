package apap.ti._5.tour_package_2306275203_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan")
@SQLDelete(sql = "UPDATE plan SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "start_location", nullable = false)
    private String startLocation;

    @Column(name = "end_location", nullable = false)
    private String endLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", referencedColumnName = "id")
    private TourPackage tourPackage;

    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderedQuantity> listOrderedQuantity;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}