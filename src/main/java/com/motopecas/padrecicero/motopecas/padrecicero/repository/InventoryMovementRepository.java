package com.motopecas.padrecicero.motopecas.padrecicero.repository;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    @Query("""
            SELECT movement
            FROM InventoryMovement movement
            WHERE movement.part.id = :partId
            ORDER BY movement.transactionDate DESC, movement.id DESC
            """)
    Page<InventoryMovement> findByPartId(Long partId, Pageable pageable);
}
