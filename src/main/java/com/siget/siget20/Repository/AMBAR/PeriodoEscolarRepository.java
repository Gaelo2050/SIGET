package com.siget.siget20.Repository.AMBAR;


import com.siget.siget20.Repository.AMBAR.Entity.PeriodoEscolar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodoEscolarRepository extends JpaRepository<PeriodoEscolar, Integer> {
}