package com.scqms.repository;

import com.scqms.entity.Cab;
import com.scqms.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CabRepository extends JpaRepository<Cab, Long> {

    Optional<Cab> findFirstByStatus(Status status);

    // ✅ Correct generic typing here:
    Optional<Cab> findFirstByStatusIn(List<Status> statuses);

    // (Optional improvement)
    Optional<Cab> findFirstByStatusInOrderByLastUpdatedAsc(List<Status> statuses);
}
