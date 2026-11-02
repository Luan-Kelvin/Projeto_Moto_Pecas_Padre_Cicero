package com.motopecas.padrecicero.motopecas.padrecicero.repository;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    Optional<Part> findByPartCode(String partCode);

    boolean existsByNameAndMark(String name, String mark);

    @Modifying
    @Query(value = """
    UPDATE part_code_sequence
    SET value = LAST_INSERT_ID(value + 10)
    WHERE id = 1
    """, nativeQuery = true)
    void incrementPartCode();

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long generatePartCode();

    Page<Part> findByNameContaining(String name);
}
