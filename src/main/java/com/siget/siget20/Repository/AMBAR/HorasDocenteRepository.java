package com.siget.siget20.Repository.AMBAR;

import com.siget.siget20.Repository.AMBAR.Entity.HorasDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface HorasDocenteRepository extends JpaRepository<HorasDocente, Long> {

    @Query("""
           SELECT SUM(h.horasTrabajadas)
           FROM HorasDocente h
           WHERE h.docente.docenteId = :docenteId
           """)
    BigDecimal totalHorasPorDocente(@Param("docenteId") Long docenteId);
}
