package com.motopecas.padrecicero.motopecas.padrecicero.repository;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findBySaleCode(String code);

    @Modifying
    @Query(value = """
    UPDATE sale_code_sequence
    SET value = LAST_INSERT_ID(value + 10)
    WHERE id = 1
    """, nativeQuery = true)
    void incrementSaleCode();

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long generateSaleCode();
}
