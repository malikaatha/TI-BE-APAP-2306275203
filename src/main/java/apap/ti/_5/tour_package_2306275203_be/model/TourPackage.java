package apap.ti._5.tour_package_2306275203_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "package")
@SQLDelete(sql = "UPDATE package SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class TourPackage {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "quota", nullable = false)
    private int quota;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "tourPackage", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Plan> listPlan;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}