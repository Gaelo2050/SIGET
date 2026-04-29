package com.siget.siget20.Repository.AMBAR;

import com.siget.siget20.Repository.AMBAR.Entity.ExclusividadLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExclusividadLaboralRepository extends JpaRepository<ExclusividadLaboral, Long> {
}