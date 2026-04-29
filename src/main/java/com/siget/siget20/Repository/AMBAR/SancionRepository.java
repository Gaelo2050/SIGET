package com.siget.siget20.Repository.AMBAR;


import com.siget.siget20.Repository.AMBAR.Entity.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {
}