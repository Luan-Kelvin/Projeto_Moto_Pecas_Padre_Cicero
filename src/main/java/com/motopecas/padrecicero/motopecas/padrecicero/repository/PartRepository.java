package com.motopecas.padrecicero.motopecas.padrecicero.repository;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long>, JpaSpecificationExecutor<Part> {

    Optional<Part> findByPartCode(String partCode);

    boolean existsByNameAndMark(String name, String mark);

    boolean existsByNameAndMarkAndIdNot(String name, String mark, Long id);

    @Modifying
    @Query(value = """
    UPDATE part_code_sequence
    SET value = LAST_INSERT_ID(value + 10)
    WHERE id = 1
    """, nativeQuery = true)
    void incrementPartCode();

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long generatePartCode();

    Page<Part> findByNameContaining(String name, Pageable pageable);

    @Query("""
            SELECT p 
            FROM Part p
            JOIN p.stock s
            WHERE s.currentQuantity < p.minimumStock 
            """)
    Page<Part> searchForItemsWithLowStock(Pageable pageable);

}
